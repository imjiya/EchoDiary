<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'DELETE') {
    header('Allow: POST, DELETE');
    respond(405, ['success' => false, 'message' => 'Use POST or DELETE to remove an entry.']);
}

$userId = getAuthenticatedUserId($pdo);
$data = getJsonInput();
$id = (int)($data['id'] ?? $_GET['id'] ?? 0);

if ($id <= 0) {
    respond(400, ['success' => false, 'message' => 'Valid entry ID is required.']);
}

try {
    $stmt = $pdo->prepare('DELETE FROM diary_entries WHERE id = :id AND user_id = :user_id');
    $stmt->execute(['id' => $id, 'user_id' => $userId]);

    if ($stmt->rowCount() === 0) {
        respond(404, ['success' => false, 'message' => 'Entry not found or already deleted.']);
    }

    respond(200, [
        'success' => true,
        'message' => 'Diary entry deleted successfully.',
        'deleted_id' => $id
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to delete diary entry.']);
}

