<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to list photo memories.']);
}

$userId = getAuthenticatedUserId($pdo);

try {
    $stmt = $pdo->prepare(
        'SELECT id, title, description, entry_date, category, category_icon, mood, photo_url
         FROM diary_entries
         WHERE user_id = :user_id AND photo_url IS NOT NULL AND photo_url != ""
         ORDER BY entry_date DESC, id DESC'
    );
    $stmt->execute(['user_id' => $userId]);
    $rows = $stmt->fetchAll();

    $photos = array_map(function ($row) {
        $ts = strtotime($row['entry_date']);
        return [
            'entry_id' => (int)$row['id'],
            'photo_url' => getBaseUrl() . '/' . $row['photo_url'],
            'image_caption' => "{$row['title']} · Entry #{$row['id']}",
            'image_subcaption' => "Photo associated with this diary entry.",
            'date_header' => date('F j, Y', $ts),
            'entry_title' => $row['title'],
            'entry_subtitle' => "{$row['category']} · " . date('M j, Y', $ts),
            'entry_description' => $row['description'],
            'category' => $row['category'],
            'icon' => $row['category_icon'] ?: '📝',
            'mood' => $row['mood']
        ];
    }, $rows);

    respond(200, [
        'success' => true,
        'header_title' => 'A moment, in a picture.',
        'header_subtitle' => 'Photos connected to your diary entries.',
        'count' => count($photos),
        'photos' => $photos
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to retrieve photo memories.']);
}

