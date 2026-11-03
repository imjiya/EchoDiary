<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET for day entries.']);
}

$userId = getAuthenticatedUserId($pdo);

if (!empty($_GET['date'])) {
    $dateStr = (string)$_GET['date'];
} elseif (!empty($_GET['year']) && !empty($_GET['month']) && !empty($_GET['day'])) {
    $dateStr = sprintf('%04d-%02d-%02d', (int)$_GET['year'], (int)$_GET['month'], (int)$_GET['day']);
} else {
    $dateStr = date('Y-m-d');
}

$ts = strtotime($dateStr);
if (!$ts) {
    respond(400, ['success' => false, 'message' => 'Invalid date format.']);
}

$formattedDate = date('Y-m-d', $ts);
$dayOfWeek = strtoupper(date('l', $ts));
$fullDate = date('F j, Y', $ts);

try {
    $stmt = $pdo->prepare(
        'SELECT id, title, description, transcript, entry_date, category, category_icon, mood, photo_url, audio_url, source_type
         FROM diary_entries
         WHERE user_id = :user_id AND entry_date = :entry_date
         ORDER BY id DESC'
    );
    $stmt->execute([
        'user_id' => $userId,
        'entry_date' => $formattedDate
    ]);
    $rows = $stmt->fetchAll();

    $entries = array_map(function ($row) use ($ts) {
        return [
            'id' => (int)$row['id'],
            'title' => $row['title'],
            'description' => $row['description'],
            'category' => $row['category'],
            'icon' => $row['category_icon'] ?: '📝',
            'date' => date('M j, Y', $ts),
            'entry_date' => $row['entry_date'],
            'mood' => $row['mood'],
            'has_photo' => !empty($row['photo_url']),
            'photo_url' => $row['photo_url'] ? getBaseUrl() . '/' . $row['photo_url'] : null
        ];
    }, $rows);

    $entryCount = count($entries);
    $categoryList = array_values(array_unique(array_filter(array_column($entries, 'category'))));
    $catText = !empty($categoryList) ? implode(', ', $categoryList) : 'None';

    $entrySummary = $entryCount === 1
        ? "1 diary entry · {$catText}"
        : "{$entryCount} diary entries · {$catText}";

    respond(200, [
        'success' => true,
        'date' => $formattedDate,
        'day_of_week' => $dayOfWeek,
        'full_date' => $fullDate,
        'entry_summary' => $entrySummary,
        'count' => $entryCount,
        'entries' => $entries
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to fetch day entries.']);
}

