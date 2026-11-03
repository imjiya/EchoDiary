<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'PUT') {
    header('Allow: POST, PUT');
    respond(405, ['success' => false, 'message' => 'Use POST or PUT to edit memory.']);
}

$userId = getAuthenticatedUserId($pdo);
$data = getJsonInput();

$id = (int)($data['id'] ?? $_GET['id'] ?? 0);
$title = trim((string)($data['title'] ?? $data['memory_title'] ?? ''));
$narrative = trim((string)($data['narrative'] ?? $data['weekly_narrative'] ?? ''));

if ($id <= 0) {
    respond(400, ['success' => false, 'message' => 'Valid memory ID is required.']);
}

if ($title === '' && $narrative === '') {
    respond(400, ['success' => false, 'message' => 'Title or narrative is required to update.']);
}

try {
    $updates = [];
    $params = ['id' => $id, 'user_id' => $userId];

    if ($title !== '') {
        $updates[] = 'title = :title';
        $params['title'] = $title;
    }

    if ($narrative !== '') {
        $updates[] = 'narrative = :narrative';
        $params['narrative'] = $narrative;
    }

    $sql = 'UPDATE memories SET ' . implode(', ', $updates) . ' WHERE id = :id AND user_id = :user_id';
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);

    if ($stmt->rowCount() === 0) {
        // Either unchanged or not found
        $chk = $pdo->prepare('SELECT id FROM memories WHERE id = :id AND user_id = :user_id');
        $chk->execute(['id' => $id, 'user_id' => $userId]);
        if (!$chk->fetch()) {
            respond(404, ['success' => false, 'message' => 'Memory not found.']);
        }
    }

    respond(200, [
        'success' => true,
        'message' => 'Memory updated successfully.',
        'memory' => [
            'id' => $id,
            'title' => $title,
            'narrative' => $narrative
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to update memory.']);
}

