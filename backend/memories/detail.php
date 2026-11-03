<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to view memory details.']);
}

$userId = getAuthenticatedUserId($pdo);
$id = (int)($_GET['id'] ?? 0);

if ($id <= 0) {
    respond(400, ['success' => false, 'message' => 'Valid memory ID is required.']);
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, type, start_date, end_date, date_range_label, title, narrative, highlights, mood_summary, status, created_at
         FROM memories
         WHERE id = :id AND user_id = :user_id
         LIMIT 1'
    );
    $stmt->execute(['id' => $id, 'user_id' => $userId]);
    $memory = $stmt->fetch();

    if (!$memory) {
        respond(404, ['success' => false, 'message' => 'Memory not found.']);
    }

    $highlights = !empty($memory['highlights']) ? json_decode($memory['highlights'], true) : [];
    if (!is_array($highlights)) {
        $highlights = [];
    }

    // Fetch linked source entries
    $entryStmt = $pdo->prepare(
        'SELECT de.id, de.title, de.category, de.category_icon, de.entry_date, de.mood, de.photo_url, de.description
         FROM diary_entries de
         INNER JOIN memory_entries me ON me.entry_id = de.id
         WHERE me.memory_id = :memory_id
         ORDER BY de.entry_date ASC'
    );
    $entryStmt->execute(['memory_id' => $id]);
    $entryRows = $entryStmt->fetchAll();

    $entries = array_map(function ($row) {
        $ts = strtotime($row['entry_date']);
        $dateFormatted = date('M j, Y', $ts);
        return [
            'id' => (int)$row['id'],
            'title' => $row['title'],
            'subtitle' => "{$dateFormatted} · {$row['category']}",
            'date' => $dateFormatted,
            'category' => $row['category'],
            'icon' => $row['category_icon'] ?: '📝',
            'description' => $row['description'],
            'mood' => $row['mood'],
            'has_photo' => !empty($row['photo_url']),
            'photo_url' => $row['photo_url'] ? getBaseUrl() . '/' . $row['photo_url'] : null
        ];
    }, $entryRows);

    respond(200, [
        'success' => true,
        'memory' => [
            'id' => (int)$memory['id'],
            'type' => $memory['type'],
            'tag_text' => strtoupper($memory['status']) . ' · ' . $memory['date_range_label'],
            'date_range_label' => $memory['date_range_label'],
            'title' => $memory['title'],
            'narrative' => $memory['narrative'],
            'summary_text' => $memory['narrative'],
            'highlights' => $highlights,
            'mood_summary' => $memory['mood_summary'],
            'entries' => $entries,
            'entries_count' => count($entries),
            'status' => $memory['status']
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load memory details.']);
}

