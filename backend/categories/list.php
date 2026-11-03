<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to list categories.']);
}

try {
    $stmt = $pdo->query('SELECT id, name, icon FROM categories ORDER BY id ASC');
    $categories = $stmt->fetchAll();

    respond(200, [
        'success' => true,
        'count' => count($categories),
        'categories' => $categories
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to fetch categories.']);
}

