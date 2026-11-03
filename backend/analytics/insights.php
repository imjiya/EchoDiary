<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET for activity and mood insights.']);
}

$userId = getAuthenticatedUserId($pdo);
$tab = ucfirst(strtolower(trim((string)($_GET['tab'] ?? 'Week'))));

if ($tab === 'Month') {
    $startDate = date('Y-m-01');
    $endDate = date('Y-m-t');
    $dateLabel = date('F Y');
} elseif ($tab === 'Year') {
    $startDate = date('Y-01-01');
    $endDate = date('Y-12-31');
    $dateLabel = date('Y');
} else { // Week
    $tab = 'Week';
    $startDate = date('Y-m-d', strtotime('monday this week'));
    $endDate = date('Y-m-d', strtotime('sunday this week'));
    $dateLabel = date('M j', strtotime($startDate)) . '–' . date('j, Y', strtotime($endDate));
}

try {
    // Fetch all entries in range
    $stmt = $pdo->prepare(
        'SELECT id, title, description, category, category_icon, mood, entry_date, photo_url
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

    $totalEntries = count($entries);

    // 1. Category breakdown
    $categoryCounts = [];
    $categoryIcons = [];
    foreach ($entries as $e) {
        $c = $e['category'] ?: 'Other';
        $categoryCounts[$c] = ($categoryCounts[$c] ?? 0) + 1;
        $categoryIcons[$c] = $e['category_icon'] ?: '📝';
    }

    $categoriesList = [];
    $maxCat = max(array_merge([1], array_values($categoryCounts)));
    foreach ($categoryCounts as $catName => $count) {
        $categoriesList[] = [
            'name' => $catName,
            'count' => $count,
            'icon' => $categoryIcons[$catName] ?? '📝',
            'progress' => round($count / $maxCat, 2)
        ];
    }

    // 2. Mood breakdown
    $moodCounts = [];
    $firstMoodEntry = null;
    $explicitCount = 0;

    foreach ($entries as $e) {
        $m = trim((string)$e['mood']);
        $isSpecified = ($m !== '' && strtolower($m) !== 'not specified' && strtolower($m) !== 'unspecified');
        if ($isSpecified) {
            $moodCounts[$m] = ($moodCounts[$m] ?? 0) + 1;
            $explicitCount++;
            if ($firstMoodEntry === null) {
                $firstMoodEntry = $e;
            }
        }
    }

    $unspecifiedCount = $totalEntries - $explicitCount;
    if ($unspecifiedCount > 0) {
        $moodCounts['Unspecified'] = $unspecifiedCount;
    }

    $moodBreakdown = [];
    foreach ($moodCounts as $mName => $mCnt) {
        $prog = $totalEntries > 0 ? round($mCnt / $totalEntries, 2) : 0;
        $moodBreakdown[] = [
            'name' => $mName,
            'count' => $mCnt,
            'total' => $totalEntries,
            'progress' => $prog
        ];
    }

    // Sort: specified first
    usort($moodBreakdown, function ($a, $b) {
        if ($a['name'] === 'Unspecified') return 1;
        if ($b['name'] === 'Unspecified') return -1;
        return $b['count'] <=> $a['count'];
    });

    // 3. Source text
    if ($firstMoodEntry) {
        $ts = strtotime($firstMoodEntry['entry_date']);
        $sourceText = "Source: Entry #{$firstMoodEntry['id']} · {$firstMoodEntry['title']} · " . date('M j, Y', $ts) . " →";
        $moodNote = "{$firstMoodEntry['mood']}: {$explicitCount} explicit mood. {$unspecifiedCount} entries unspecified.";
        $moodEvidence = "Mood evidence: {$firstMoodEntry['mood']} on Entry #{$firstMoodEntry['id']}. No mood inferred for other entries.";
    } else {
        $sourceText = "No mood specified in recorded entries.";
        $moodNote = "{$totalEntries} entries recorded with unspecified mood.";
        $moodEvidence = "No explicit mood recorded for this time range.";
    }

    // 4. Analysis items (for AnalysisResultsScreen)
    $analysisItems = array_map(function ($e) {
        $ts = strtotime($e['entry_date']);
        $dateStr = date('M j, Y', $ts);
        $moodTag = (!empty($e['mood']) && strtolower($e['mood']) !== 'not specified') ? " · {$e['mood']} recorded" : "";
        return [
            'entry_id' => (int)$e['id'],
            'title' => "{$e['category']} · {$e['title']}{$moodTag}",
            'sublink' => "View Entry #{$e['id']}: {$e['title']} · {$dateStr}",
            'icon' => $e['category_icon'] ?: '📝'
        ];
    }, $entries);

    respond(200, [
        'success' => true,
        'selected_tab' => $tab,
        'date_range_text' => "{$dateLabel} · " . ($totalEntries > 0 ? "Based on {$totalEntries} recorded entries" : "No entries recorded"),
        'header_subtitle' => strtoupper($dateLabel) . " · BASED ON {$totalEntries} ENTRIES",
        'title' => 'Grounded in your words.',
        'description' => count($categoriesList) . " categories, {$totalEntries} total entries. Topics below come directly from your saved diary.",
        'activities_count' => $totalEntries,
        'categories_count' => count($categoriesList),
        'categories' => $categoriesList,
        'mood_breakdown' => $moodBreakdown,
        'mood_note' => $moodNote,
        'source_text' => $sourceText,
        'source_entry_id' => $firstMoodEntry ? (int)$firstMoodEntry['id'] : null,
        'footer_text' => 'No mood is inferred from the entry descriptions.',
        'mood_evidence_text' => $moodEvidence,
        'items' => $analysisItems
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load insights.']);
}

