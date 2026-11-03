<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to view entry details.']);
}

$userId = getAuthenticatedUserId($pdo);
$id = (int)($_GET['id'] ?? 0);

if ($id <= 0) {
    respond(400, ['success' => false, 'message' => 'Valid entry ID is required.']);
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, title, description, transcript, entry_date, category, category_icon, mood, photo_url, audio_url, source_type, created_at
         FROM diary_entries
         WHERE id = :id AND user_id = :user_id
         LIMIT 1'
    );
    $stmt->execute(['id' => $id, 'user_id' => $userId]);
    $entry = $stmt->fetch();

    if (!$entry) {
        respond(404, ['success' => false, 'message' => 'Diary entry not found.']);
    }

    // Check if included in any saved memory
    $memStmt = $pdo->prepare(
        'SELECT m.id, m.title, m.date_range_label
         FROM memories m
         INNER JOIN memory_entries me ON me.memory_id = m.id
         WHERE me.entry_id = :entry_id
         LIMIT 1'
    );
    $memStmt->execute(['entry_id' => $id]);
    $memory = $memStmt->fetch();

    $ts = strtotime($entry['entry_date']);
    $photoStatus = !empty($entry['photo_url']) ? 'Photo attached' : 'No photo attached';
    $memoryBadge = $memory
        ? "Entry #{$entry['id']} · Included in your {$memory['date_range_label']} memory"
        : "Entry #{$entry['id']}";

    respond(200, [
        'success' => true,
        'entry' => [
            'id' => (int)$entry['id'],
            'title' => $entry['title'],
            'description' => $entry['description'],
            'transcript' => $entry['transcript'],
            'category' => $entry['category'],
            'category_icon' => $entry['category_icon'] ?: '📝',
            'icon' => $entry['category_icon'] ?: '📝',
            'date' => date('l, M j, Y', $ts), // e.g. "Monday, Oct 5, 2026"
            'short_date' => date('M j, Y', $ts), // e.g. "Oct 5, 2026"
            'entry_date' => $entry['entry_date'],
            'mood' => $entry['mood'],
            'photo_status' => $photoStatus,
            'photo_url' => $entry['photo_url'] ? getBaseUrl() . '/' . $entry['photo_url'] : null,
            'audio_url' => $entry['audio_url'] ? getBaseUrl() . '/' . $entry['audio_url'] : null,
            'has_photo' => !empty($entry['photo_url']),
            'has_audio' => !empty($entry['audio_url']),
            'entry_badge' => $memoryBadge,
            'source_type' => $entry['source_type']
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load entry details.']);
}

