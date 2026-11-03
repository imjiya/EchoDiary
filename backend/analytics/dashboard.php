<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET for home dashboard.']);
}

$userId = getAuthenticatedUserId($pdo);

try {
    // 1. Fetch user details
    $userStmt = $pdo->prepare('SELECT full_name FROM users WHERE id = :id LIMIT 1');
    $userStmt->execute(['id' => $userId]);
    $fullName = (string)$userStmt->fetchColumn() ?: 'User';
    $firstName = explode(' ', trim($fullName))[0];

    // 2. Fetch recent activities (up to 5)
    $recStmt = $pdo->prepare(
        'SELECT id, title, category, category_icon, entry_date, description, photo_url
         FROM diary_entries
         WHERE user_id = :user_id
         ORDER BY entry_date DESC, id DESC
         LIMIT 5'
    );
    $recStmt->execute(['user_id' => $userId]);
    $recentRows = $recStmt->fetchAll();

    $recentActivities = array_map(function ($r) {
        $ts = strtotime($r['entry_date']);
        return [
            'id' => (int)$r['id'],
            'title' => $r['title'],
            'subtitle' => "{$r['category']} · " . date('M j, Y', $ts),
            'category' => $r['category'],
            'date' => date('M j, Y', $ts),
            'icon' => $r['category_icon'] ?: '📝',
            'description' => $r['description'],
            'photo_url' => $r['photo_url'] ? getBaseUrl() . '/' . $r['photo_url'] : null
        ];
    }, $recentRows);

    // 3. Weekly memory info
    $memStmt = $pdo->prepare(
        "SELECT id, title, narrative, date_range_label 
         FROM memories 
         WHERE user_id = :user_id AND type = 'weekly'
         ORDER BY start_date DESC LIMIT 1"
    );
    $memStmt->execute(['user_id' => $userId]);
    $latestMemory = $memStmt->fetch();

    // Calculate this week's entry and category counts
    $mon = date('Y-m-d', strtotime('monday this week'));
    $sun = date('Y-m-d', strtotime('sunday this week'));
    $statStmt = $pdo->prepare(
        'SELECT COUNT(*) as entry_count, COUNT(DISTINCT category) as category_count
         FROM diary_entries
         WHERE user_id = :user_id AND entry_date BETWEEN :start AND :end'
    );
    $statStmt->execute(['user_id' => $userId, 'start' => $mon, 'end' => $sun]);
    $weekStats = $statStmt->fetch();
    $weekEntries = (int)($weekStats['entry_count'] ?? 0);
    $weekCategories = (int)($weekStats['category_count'] ?? 0);

    // Saved memories total count
    $savedMemStmt = $pdo->prepare("SELECT COUNT(*) FROM memories WHERE user_id = :user_id AND status = 'saved'");
    $savedMemStmt->execute(['user_id' => $userId]);
    $savedMemCount = (int)$savedMemStmt->fetchColumn();

    $weeklyTag = $latestMemory ? "WEEKLY MEMORY · {$latestMemory['date_range_label']}" : "WEEKLY MEMORY · " . strtoupper(date('M j', strtotime($mon))) . '–' . date('j', strtotime($sun));
    $weeklyTitle = $latestMemory ? $latestMemory['title'] : "A week of progress and connection.";
    $weeklyStatsText = "{$weekEntries} entries · {$weekCategories} categories · {$savedMemCount} saved memory";

    respond(200, [
        'success' => true,
        'user_name' => $firstName,
        'full_name' => $fullName,
        'date_text' => date('l, M j, Y'), // e.g. "Sunday, Oct 11, 2026"
        'subtitle_text' => 'A little reflection goes a long way.',
        'weekly_memory' => [
            'id' => $latestMemory ? (int)$latestMemory['id'] : null,
            'tag' => $weeklyTag,
            'title' => $weeklyTitle,
            'stats_text' => $weeklyStatsText
        ],
        'recent_activities' => $recentActivities
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load home dashboard.']);
}

