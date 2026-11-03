<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, ['success' => false, 'message' => 'Use POST to create an entry.']);
}

$userId = getAuthenticatedUserId($pdo);
$data = getJsonInput();

$title = trim((string)($data['title'] ?? ''));
$description = trim((string)($data['description'] ?? ''));
$transcript = trim((string)($data['transcript'] ?? ''));
$entryDate = trim((string)($data['entry_date'] ?? $data['date'] ?? date('Y-m-d')));
$category = trim((string)($data['category'] ?? 'Other'));
$mood = trim((string)($data['mood'] ?? 'Not specified'));
$sourceType = strtolower(trim((string)($data['source_type'] ?? 'text')));

if ($title === '' && $description === '') {
    respond(400, ['success' => false, 'message' => 'Title or description is required.']);
}

if ($title === '') {
    // If no title, take first 40 chars of description
    $title = mb_substr($description, 0, 40) . (mb_strlen($description) > 40 ? '...' : '');
}

// Normalize entryDate
$timestamp = strtotime($entryDate);
$formattedDate = $timestamp ? date('Y-m-d', $timestamp) : date('Y-m-d');

// Determine category icon
$catStmt = $pdo->prepare('SELECT icon FROM categories WHERE name = :name LIMIT 1');
$catStmt->execute(['name' => $category]);
$catIcon = $catStmt->fetchColumn() ?: '📝';

// Handle photo upload if present
$photoUrl = null;
if (isset($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
    $file = $_FILES['photo'];
    $ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $allowed = ['jpg', 'jpeg', 'png', 'webp'];
    if (in_array($ext, $allowed, true)) {
        $fileName = 'photo_' . $userId . '_' . uniqid() . '.' . $ext;
        $dest = __DIR__ . '/../uploads/' . $fileName;
        if (move_uploaded_file($file['tmp_name'], $dest)) {
            $photoUrl = 'uploads/' . $fileName;
        }
    }
}

// Handle audio upload if present
$audioUrl = null;
if (isset($_FILES['audio']) && $_FILES['audio']['error'] === UPLOAD_ERR_OK) {
    $file = $_FILES['audio'];
    $ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
    $allowed = ['mp3', 'm4a', 'wav', 'aac', '3gp', 'ogg'];
    if (in_array($ext, $allowed, true)) {
        $fileName = 'audio_' . $userId . '_' . uniqid() . '.' . $ext;
        $dest = __DIR__ . '/../uploads/' . $fileName;
        if (move_uploaded_file($file['tmp_name'], $dest)) {
            $audioUrl = 'uploads/' . $fileName;
        }
    }
}

try {
    $stmt = $pdo->prepare(
        'INSERT INTO diary_entries 
        (user_id, title, description, transcript, entry_date, category, category_icon, mood, photo_url, audio_url, source_type)
        VALUES 
        (:user_id, :title, :description, :transcript, :entry_date, :category, :category_icon, :mood, :photo_url, :audio_url, :source_type)'
    );

    $stmt->execute([
        'user_id' => $userId,
        'title' => $title,
        'description' => $description,
        'transcript' => $transcript !== '' ? $transcript : null,
        'entry_date' => $formattedDate,
        'category' => $category,
        'category_icon' => $catIcon,
        'mood' => $mood,
        'photo_url' => $photoUrl,
        'audio_url' => $audioUrl,
        'source_type' => in_array($sourceType, ['text', 'voice'], true) ? $sourceType : 'text'
    ]);

    $entryId = (int)$pdo->lastInsertId();

    respond(201, [
        'success' => true,
        'message' => 'Diary entry created successfully.',
        'entry' => [
            'id' => $entryId,
            'title' => $title,
            'description' => $description,
            'transcript' => $transcript,
            'date' => date('M j, Y', strtotime($formattedDate)),
            'entry_date' => $formattedDate,
            'category' => $category,
            'category_icon' => $catIcon,
            'mood' => $mood,
            'has_photo' => $photoUrl !== null,
            'photo_url' => $photoUrl ? getBaseUrl() . '/' . $photoUrl : null,
            'audio_url' => $audioUrl ? getBaseUrl() . '/' . $audioUrl : null,
            'source_type' => $sourceType
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to create diary entry.']);
}

