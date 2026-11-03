<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

$userId = getAuthenticatedUserId($pdo);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'DELETE' || ($method === 'POST' && (($_POST['action'] ?? '') === 'clear'))) {
    // Clear recent searches
    try {
        $stmt = $pdo->prepare('DELETE FROM recent_searches WHERE user_id = :user_id');
        $stmt->execute(['user_id' => $userId]);
        respond(200, ['success' => true, 'message' => 'Recent searches cleared.']);
    } catch (Throwable $e) {
        error_log($e->getMessage());
        respond(500, ['success' => false, 'message' => 'Failed to clear recent searches.']);
    }
}

if ($method !== 'GET') {
    header('Allow: GET, DELETE, POST');
    respond(405, ['success' => false, 'message' => 'Method not allowed.']);
}

$query = trim((string)($_GET['query'] ?? $_GET['searchQuery'] ?? ''));

try {
    // If a search query is provided, record it in recent_searches (avoiding consecutive duplicates)
    if ($query !== '') {
        $lastCheck = $pdo->prepare('SELECT query FROM recent_searches WHERE user_id = :user_id ORDER BY id DESC LIMIT 1');
        $lastCheck->execute(['user_id' => $userId]);
        $lastQuery = $lastCheck->fetchColumn();

        if ($lastQuery !== $query) {
            $insertSearch = $pdo->prepare('INSERT INTO recent_searches (user_id, query) VALUES (:user_id, :query)');
            $insertSearch->execute(['user_id' => $userId, 'query' => $query]);
        }
    }

    // Fetch top 5 recent unique searches
    $recentStmt = $pdo->prepare(
        'SELECT DISTINCT query FROM recent_searches WHERE user_id = :user_id ORDER BY id DESC LIMIT 5'
    );
    $recentStmt->execute(['user_id' => $userId]);
    $recentSearches = $recentStmt->fetchAll(PDO::FETCH_COLUMN);

    $results = [];
    if ($query !== '') {
        $searchStmt = $pdo->prepare(
            'SELECT id, title, description, entry_date, category, category_icon, mood, photo_url
             FROM diary_entries
             WHERE user_id = :user_id 
               AND (title LIKE :q OR description LIKE :q OR category LIKE :q)
             ORDER BY entry_date DESC
             LIMIT 30'
        );
        $searchStmt->execute(['user_id' => $userId, 'q' => '%' . $query . '%']);
        $rows = $searchStmt->fetchAll();

        $results = array_map(function ($row) {
            $ts = strtotime($row['entry_date']);
            return [
                'id' => (int)$row['id'],
                'title' => $row['title'],
                'category' => $row['category'],
                'date' => date('M j, Y', $ts),
                'description' => $row['description'],
                'icon' => $row['category_icon'] ?: '📝',
                'has_photo' => !empty($row['photo_url']),
                'photo_url' => $row['photo_url'] ? getBaseUrl() . '/' . $row['photo_url'] : null
            ];
        }, $rows);
    }

    respond(200, [
        'success' => true,
        'query' => $query,
        'search_result_count' => count($results),
        'results' => $results,
        'recent_searches' => $recentSearches
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Search error.']);
}

