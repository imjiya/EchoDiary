<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET for calendar month overview.']);
}

$userId = getAuthenticatedUserId($pdo);

$year = (int)($_GET['year'] ?? date('Y'));
$month = (int)($_GET['month'] ?? date('n'));

if ($year < 2000 || $year > 2100 || $month < 1 || $month > 12) {
    respond(400, ['success' => false, 'message' => 'Invalid year or month.']);
}

$startDate = sprintf('%04d-%02d-01', $year, $month);
$daysInMonth = (int)date('t', strtotime($startDate));
$endDate = sprintf('%04d-%02d-%02d', $year, $month, $daysInMonth);

try {
    $stmt = $pdo->prepare(
        'SELECT entry_date, DAY(entry_date) as day, COUNT(*) as entry_count, 
                GROUP_CONCAT(DISTINCT category SEPARATOR ", ") as categories
         FROM diary_entries
         WHERE user_id = :user_id AND entry_date BETWEEN :start_date AND :end_date
         GROUP BY entry_date
         ORDER BY entry_date ASC'
    );
    $stmt->execute([
        'user_id' => $userId,
        'start_date' => $startDate,
        'end_date' => $endDate
    ]);
    $rows = $stmt->fetchAll();

    $activeDays = [];
    foreach ($rows as $r) {
        $activeDays[(int)$r['day']] = [
            'date' => $r['entry_date'],
            'day' => (int)$r['day'],
            'entry_count' => (int)$r['entry_count'],
            'categories' => $r['categories']
        ];
    }

    respond(200, [
        'success' => true,
        'year' => $year,
        'month' => $month,
        'month_name' => date('F', strtotime($startDate)),
        'total_entries_this_month' => array_sum(array_column($activeDays, 'entry_count')),
        'days_with_entries' => array_keys($activeDays),
        'active_days' => $activeDays
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load calendar month overview.']);
}

