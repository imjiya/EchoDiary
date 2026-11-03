<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET to list entries.']);
}

$userId = getAuthenticatedUserId($pdo);

$search = trim((string)($_GET['search'] ?? ''));
$category = trim((string)($_GET['category'] ?? ''));
$mood = trim((string)($_GET['mood'] ?? ''));
$source = strtolower(trim((string)($_GET['source'] ?? '')));
$fromDate = trim((string)($_GET['from_date'] ?? ''));
$toDate = trim((string)($_GET['to_date'] ?? ''));
$specificDate = trim((string)($_GET['date'] ?? ''));
$limit = max(1, min(100, (int)($_GET['limit'] ?? 50)));
$offset = max(0, (int)($_GET['offset'] ?? 0));

try {
    $where = ['user_id = :user_id'];
    $params = ['user_id' => $userId];

    if ($search !== '') {
        $where[] = '(title LIKE :search OR description LIKE :search OR transcript LIKE :search)';
        $params['search'] = '%' . $search . '%';
    }

    if ($category !== '' && strtolower($category) !== 'all') {
        $where[] = 'category = :category';
        $params['category'] = $category;
    }

    if ($mood !== '' && strtolower($mood) !== 'all') {
        if (strtolower($mood) === 'unspecified') {
            $where[] = "(mood = 'Not specified' OR mood = 'Unspecified' OR mood = '' OR mood IS NULL)";
        } else {
            $where[] = 'mood = :mood';
            $params['mood'] = $mood;
        }
    }

    if ($source !== '' && strtolower($source) !== 'all') {
        $where[] = 'source_type = :source';
        $params['source'] = $source;
    }

    if ($specificDate !== '') {
        $where[] = 'entry_date = :specific_date';
        $params['specific_date'] = date('Y-m-d', strtotime($specificDate));
    } else {
        if ($fromDate !== '') {
            $where[] = 'entry_date >= :from_date';
            $params['from_date'] = date('Y-m-d', strtotime($fromDate));
        }
        if ($toDate !== '') {
            $where[] = 'entry_date <= :to_date';
            $params['to_date'] = date('Y-m-d', strtotime($toDate));
        }
    }

    $whereClause = implode(' AND ', $where);

    // Count total matching
    $countStmt = $pdo->prepare("SELECT COUNT(*) FROM diary_entries WHERE $whereClause");
    $countStmt->execute($params);
    $totalCount = (int)$countStmt->fetchColumn();

    // Query entries
    $sql = "SELECT id, title, description, transcript, entry_date, category, category_icon, mood, photo_url, audio_url, source_type, created_at
            FROM diary_entries
            WHERE $whereClause
            ORDER BY entry_date DESC, id DESC
            LIMIT :limit OFFSET :offset";

    $stmt = $pdo->prepare($sql);
    foreach ($params as $k => $v) {
        $stmt->bindValue(':' . $k, $v);
    }
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);
    $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
    $stmt->execute();
    $rows = $stmt->fetchAll();

    $entries = array_map(function ($row) {
        $ts = strtotime($row['entry_date']);
        return [
            'id' => (int)$row['id'],
            'title' => $row['title'],
            'description' => $row['description'],
            'transcript' => $row['transcript'],
            'category' => $row['category'],
            'category_icon' => $row['category_icon'] ?: '📝',
            'icon' => $row['category_icon'] ?: '📝', // matches DiaryEntryItem icon field
            'date' => date('M j, Y', $ts), // matches DiaryEntryItem date format
            'day_of_week' => strtoupper(date('l', $ts)),
            'full_date' => date('F j, Y', $ts),
            'entry_date' => $row['entry_date'],
            'mood' => $row['mood'],
            'has_photo' => !empty($row['photo_url']),
            'photo_url' => $row['photo_url'] ? getBaseUrl() . '/' . $row['photo_url'] : null,
            'audio_url' => $row['audio_url'] ? getBaseUrl() . '/' . $row['audio_url'] : null,
            'source_type' => $row['source_type']
        ];
    }, $rows);

    respond(200, [
        'success' => true,
        'total' => $totalCount,
        'count' => count($entries),
        'entries' => $entries
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to fetch entries.']);
}

