<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'PUT') {
    header('Allow: POST, PUT');
    respond(405, ['success' => false, 'message' => 'Use POST or PUT to update entry.']);
}

$userId = getAuthenticatedUserId($pdo);
$data = getJsonInput();

$id = (int)($data['id'] ?? $_GET['id'] ?? 0);

if ($id <= 0) {
    respond(400, ['success' => false, 'message' => 'Valid entry ID is required.']);
}

// Check entry ownership
$check = $pdo->prepare('SELECT id, photo_url FROM diary_entries WHERE id = :id AND user_id = :user_id LIMIT 1');
$check->execute(['id' => $id, 'user_id' => $userId]);
$existing = $check->fetch();

if (!$existing) {
    respond(404, ['success' => false, 'message' => 'Diary entry not found.']);
}

$updates = [];
$params = ['id' => $id, 'user_id' => $userId];

if (isset($data['title']) && trim((string)$data['title']) !== '') {
    $updates[] = 'title = :title';
    $params['title'] = trim((string)$data['title']);
}

if (isset($data['description'])) {
    $updates[] = 'description = :description';
    $params['description'] = trim((string)$data['description']);
}

if (isset($data['transcript'])) {
    $updates[] = 'transcript = :transcript';
    $params['transcript'] = trim((string)$data['transcript']);
}

if (!empty($data['date']) || !empty($data['entry_date'])) {
    $rawDate = (string)($data['entry_date'] ?? $data['date']);
    $ts = strtotime($rawDate);
    if ($ts) {
        $updates[] = 'entry_date = :entry_date';
        $params['entry_date'] = date('Y-m-d', $ts);
    }
}

if (!empty($data['category'])) {
    $cat = trim((string)$data['category']);
    $updates[] = 'category = :category';
    $params['category'] = $cat;

    // lookup category icon
    $catStmt = $pdo->prepare('SELECT icon FROM categories WHERE name = :name LIMIT 1');
    $catStmt->execute(['name' => $cat]);
    $catIcon = $catStmt->fetchColumn() ?: '📝';
    $updates[] = 'category_icon = :category_icon';
    $params['category_icon'] = $catIcon;
}

if (isset($data['mood'])) {
    $updates[] = 'mood = :mood';
    $params['mood'] = trim((string)$data['mood']);
}

// Handle new photo upload
if (isset($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
    $file = $_FILES['photo'];
    $ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $allowed = ['jpg', 'jpeg', 'png', 'webp'];
    if (in_array($ext, $allowed, true)) {
        $fileName = 'photo_' . $userId . '_' . uniqid() . '.' . $ext;
        $dest = __DIR__ . '/../uploads/' . $fileName;
        if (move_uploaded_file($file['tmp_name'], $dest)) {
            $updates[] = 'photo_url = :photo_url';
            $params['photo_url'] = 'uploads/' . $fileName;
        }
    }
}

if (empty($updates)) {
    respond(400, ['success' => false, 'message' => 'No fields provided to update.']);
}

try {
    $sql = 'UPDATE diary_entries SET ' . implode(', ', $updates) . ' WHERE id = :id AND user_id = :user_id';
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);

    // Fetch updated entry
    $fetchStmt = $pdo->prepare('SELECT * FROM diary_entries WHERE id = :id');
    $fetchStmt->execute(['id' => $id]);
    $entry = $fetchStmt->fetch();

    $ts = strtotime($entry['entry_date']);

    respond(200, [
        'success' => true,
        'message' => 'Diary entry updated successfully.',
        'entry' => [
            'id' => (int)$entry['id'],
            'title' => $entry['title'],
            'description' => $entry['description'],
            'category' => $entry['category'],
            'category_icon' => $entry['category_icon'],
            'icon' => $entry['category_icon'],
            'date' => date('M j, Y', $ts),
            'entry_date' => $entry['entry_date'],
            'mood' => $entry['mood'],
            'has_photo' => !empty($entry['photo_url']),
            'photo_url' => $entry['photo_url'] ? getBaseUrl() . '/' . $entry['photo_url'] : null
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to update diary entry.']);
}

