<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: POST, GET');
    respond(405, ['success' => false, 'message' => 'Use POST or GET to generate memories.']);
}

$userId = getAuthenticatedUserId($pdo);
$input = ($_SERVER['REQUEST_METHOD'] === 'POST') ? getJsonInput() : $_GET;

$type = strtolower(trim((string)($input['type'] ?? 'weekly')));
$save = filter_var($input['save'] ?? false, FILTER_VALIDATE_BOOLEAN);

// Calculate date range if not explicitly provided
if (!empty($input['start_date']) && !empty($input['end_date'])) {
    $startDate = date('Y-m-d', strtotime((string)$input['start_date']));
    $endDate = date('Y-m-d', strtotime((string)$input['end_date']));
} else {
    if ($type === 'monthly') {
        $startDate = date('Y-m-01');
        $endDate = date('Y-m-t');
    } elseif ($type === 'yearly') {
        $startDate = date('Y-01-01');
        $endDate = date('Y-12-31');
    } else { // weekly (Monday to Sunday)
        $startDate = date('Y-m-d', strtotime('monday this week'));
        $endDate = date('Y-m-d', strtotime('sunday this week'));
    }
}

try {
    // Fetch user entries in this period
    $stmt = $pdo->prepare(
        'SELECT id, title, description, entry_date, category, category_icon, mood, photo_url
         FROM diary_entries
         WHERE user_id = :user_id AND entry_date BETWEEN :start_date AND :end_date
         ORDER BY entry_date ASC'
    );
    $stmt->execute([
        'user_id' => $userId,
        'start_date' => $startDate,
        'end_date' => $endDate
    ]);
    $entries = $stmt->fetchAll();
    $entryCount = count($entries);

    if ($entryCount === 0 && empty($input['start_date'])) {
        $fallbackStmt = $pdo->prepare(
            'SELECT MIN(entry_date) as min_date, MAX(entry_date) as max_date FROM diary_entries WHERE user_id = :user_id'
        );
        $fallbackStmt->execute(['user_id' => $userId]);
        $dateBounds = $fallbackStmt->fetch();
        if ($dateBounds && !empty($dateBounds['max_date'])) {
            $maxTs = strtotime($dateBounds['max_date']);
            if ($type === 'monthly') {
                $startDate = date('Y-m-01', $maxTs);
                $endDate = date('Y-m-t', $maxTs);
            } elseif ($type === 'yearly') {
                $startDate = date('Y-01-01', $maxTs);
                $endDate = date('Y-12-31', $maxTs);
            } else {
                $startDate = date('Y-m-d', strtotime('monday this week', $maxTs));
                $endDate = date('Y-m-d', strtotime('sunday this week', $maxTs));
            }
            $stmt->execute([
                'user_id' => $userId,
                'start_date' => $startDate,
                'end_date' => $endDate
            ]);
            $entries = $stmt->fetchAll();
            $entryCount = count($entries);
        }
    }
    $categories = array_values(array_unique(array_filter(array_column($entries, 'category'))));
    $categoriesCount = count($categories);

    // Mood analysis
    $moodCounts = [];
    $explicitMoods = 0;
    foreach ($entries as $e) {
        $m = trim((string)$e['mood']);
        if ($m !== '' && strtolower($m) !== 'not specified' && strtolower($m) !== 'unspecified') {
            $moodCounts[$m] = ($moodCounts[$m] ?? 0) + 1;
            $explicitMoods++;
        }
    }

    if ($explicitMoods > 0) {
        $moodParts = [];
        foreach ($moodCounts as $mName => $mCnt) {
            $moodParts[] = "{$mCnt} recorded feeling {$mName}";
        }
        $unspecified = $entryCount - $explicitMoods;
        $moodSummary = implode(', ', $moodParts) . ". " . ($unspecified > 0 ? "The other {$unspecified} entries have no mood specified." : "All entries had moods recorded.");
    } else {
        $moodSummary = "No mood specified across these {$entryCount} entries.";
    }

    // Format human-friendly label
    $startTs = strtotime($startDate);
    $endTs = strtotime($endDate);
    if ($type === 'weekly') {
        $dateRangeLabel = strtoupper(date('M j', $startTs)) . '–' . date('j, Y', $endTs);
    } elseif ($type === 'monthly') {
        $dateRangeLabel = date('F Y', $startTs);
    } else {
        $dateRangeLabel = date('Y', $startTs);
    }

    // Synthesize grounded title and narrative
    $title = "A week of progress and connection.";
    if ($type === 'monthly') {
        $title = date('F', $startTs) . ", in your words";
    } elseif ($type === 'yearly') {
        $title = "One small chapter of " . date('Y', $startTs);
    }

    if ($entryCount > 0) {
        $titles = array_column($entries, 'title');
        $narrative = "During this period, you recorded " . implode(', ', array_slice($titles, 0, 4)) . ". " . $moodSummary;
    } else {
        $narrative = "No entries recorded for this period yet. Add daily diary entries to see your story come together.";
    }

    $highlights = array_slice(array_unique(array_filter(array_merge($categories, array_column($entries, 'title')))), 0, 4);

    $memoryId = null;
    if ($save && $entryCount > 0) {
        $pdo->beginTransaction();

        $insMem = $pdo->prepare(
            'INSERT INTO memories (user_id, type, start_date, end_date, date_range_label, title, narrative, highlights, mood_summary, status)
             VALUES (:user_id, :type, :start_date, :end_date, :label, :title, :narrative, :highlights, :mood_summary, :status)'
        );
        $insMem->execute([
            'user_id' => $userId,
            'type' => $type,
            'start_date' => $startDate,
            'end_date' => $endDate,
            'label' => $dateRangeLabel,
            'title' => $title,
            'narrative' => $narrative,
            'highlights' => json_encode($highlights, JSON_UNESCAPED_UNICODE),
            'mood_summary' => $moodSummary,
            'status' => 'saved'
        ]);
        $memoryId = (int)$pdo->lastInsertId();

        $linkStmt = $pdo->prepare('INSERT IGNORE INTO memory_entries (memory_id, entry_id) VALUES (:mid, :eid)');
        foreach ($entries as $e) {
            $linkStmt->execute(['mid' => $memoryId, 'eid' => $e['id']]);
        }

        $pdo->commit();
    }

    // Format entry items for summary
    $summaryEntries = array_map(function ($e) {
        $ts = strtotime($e['entry_date']);
        return [
            'id' => (int)$e['id'],
            'title' => $e['title'],
            'category_details' => "{$e['category']} · " . date('M j', $ts),
            'icon' => $e['category_icon'] ?: '📝'
        ];
    }, $entries);

    respond(200, [
        'success' => true,
        'saved' => $memoryId !== null,
        'memory_id' => $memoryId,
        'date_range' => $dateRangeLabel,
        'start_date' => $startDate,
        'end_date' => $endDate,
        'diary_entries_count' => $entryCount,
        'categories_count' => $categoriesCount,
        'title' => $title,
        'summary_text' => $narrative,
        'narrative' => $narrative,
        'highlights' => $highlights,
        'mood_summary' => $moodSummary,
        'entries' => $summaryEntries
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to generate weekly memory.']);
}

