<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to list memories.']);
}

$userId = getAuthenticatedUserId($pdo);
$type = strtolower(trim((string)($_GET['type'] ?? 'weekly')));

try {
    $where = ['m.user_id = :user_id'];
    $params = ['user_id' => $userId];

    if (in_array($type, ['weekly', 'monthly', 'yearly'], true)) {
        $where[] = 'm.type = :type';
        $params['type'] = $type;
    }

    $whereClause = implode(' AND ', $where);

    $sql = "SELECT m.id, m.type, m.start_date, m.end_date, m.date_range_label, m.title, m.narrative, 
                   m.highlights, m.mood_summary, m.status, m.created_at,
                   COUNT(me.entry_id) as entry_count
            FROM memories m
            LEFT JOIN memory_entries me ON me.memory_id = m.id
            WHERE $whereClause
            GROUP BY m.id
            ORDER BY m.start_date DESC, m.id DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $rows = $stmt->fetchAll();

    $memories = array_map(function ($row) use ($pdo) {
        $highlights = !empty($row['highlights']) ? json_decode($row['highlights'], true) : [];
        if (!is_array($highlights)) {
            $highlights = [];
        }

        // Count photos in this memory's entries
        $photoStmt = $pdo->prepare(
            'SELECT COUNT(*) FROM diary_entries de
             INNER JOIN memory_entries me ON me.entry_id = de.id
             WHERE me.memory_id = :memory_id AND de.photo_url IS NOT NULL'
        );
        $photoStmt->execute(['memory_id' => $row['id']]);
        $photoCount = (int)$photoStmt->fetchColumn();

        return [
            'id' => (int)$row['id'],
            'type' => $row['type'],
            'title' => $row['title'],
            'narrative' => $row['narrative'],
            'summary_text' => $row['narrative'],
            'date_range_label' => $row['date_range_label'],
            'subtitle' => "{$row['date_range_label']} · {$row['entry_count']} diary entries",
            'entry_count' => (int)$row['entry_count'],
            'photo_count' => $photoCount,
            'highlights' => $highlights,
            'mood_summary' => $row['mood_summary'],
            'status' => $row['status'],
            'start_date' => $row['start_date'],
            'end_date' => $row['end_date'],
            'created_at' => $row['created_at']
        ];
    }, $rows);

    respond(200, [
        'success' => true,
        'count' => count($memories),
        'memories' => $memories
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to fetch memories.']);
}

