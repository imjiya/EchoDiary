<?php
declare(strict_types=1);

require_once __DIR__ . '/config/config.php';

try {
    $email = 'alex.morgan@example.com';
    $password = 'password123';
    $fullName = 'Alex Morgan';

    // Check if alex.morgan exists
    $stmt = $pdo->prepare('SELECT id FROM users WHERE email = :email LIMIT 1');
    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch();

    if (!$user) {
        $hash = password_hash($password, PASSWORD_DEFAULT);
        $ins = $pdo->prepare('INSERT INTO users (full_name, email, password_hash) VALUES (:name, :email, :hash)');
        $ins->execute(['name' => $fullName, 'email' => $email, 'hash' => $hash]);
        $userId = (int)$pdo->lastInsertId();
    } else {
        $userId = (int)$user['id'];
    }

    // Ensure user_settings
    $pdo->prepare('INSERT IGNORE INTO user_settings (user_id) VALUES (:user_id)')->execute(['user_id' => $userId]);

    // Clear old sample entries for this user to avoid duplicates
    $pdo->prepare('DELETE FROM diary_entries WHERE user_id = :user_id')->execute(['user_id' => $userId]);
    $pdo->prepare('DELETE FROM memories WHERE user_id = :user_id')->execute(['user_id' => $userId]);
    $pdo->prepare('DELETE FROM recent_searches WHERE user_id = :user_id')->execute(['user_id' => $userId]);

    // Insert 5 entries matching frontend dummy data
    $entriesData = [
        [
            'title' => 'Lectures and project',
            'desc' => 'Attended lectures and worked on a project.',
            'date' => '2026-10-05',
            'cat' => 'College',
            'icon' => '🎓',
            'mood' => 'Not specified',
            'photo' => null
        ],
        [
            'title' => 'Lunch with friends',
            'desc' => 'Had lunch with friends after class.',
            'date' => '2026-10-06',
            'cat' => 'Friends',
            'icon' => '👥',
            'mood' => 'Not specified',
            'photo' => 'uploads/lunch_sample.jpg'
        ],
        [
            'title' => 'Evening walk',
            'desc' => 'Went for an evening walk.',
            'date' => '2026-10-07',
            'cat' => 'Health',
            'icon' => '❤️',
            'mood' => 'Calm',
            'photo' => null
        ],
        [
            'title' => 'Presentation preparation',
            'desc' => 'Prepared a presentation.',
            'date' => '2026-10-08',
            'cat' => 'Work',
            'icon' => '💼',
            'mood' => 'Not specified',
            'photo' => null
        ],
        [
            'title' => 'Weekend with family',
            'desc' => 'Spent time with family over the weekend.',
            'date' => '2026-10-10',
            'cat' => 'Family',
            'icon' => '🏠',
            'mood' => 'Not specified',
            'photo' => null
        ]
    ];

    $insEntry = $pdo->prepare(
        'INSERT INTO diary_entries (user_id, title, description, entry_date, category, category_icon, mood, photo_url, source_type)
         VALUES (:uid, :title, :desc, :date, :cat, :icon, :mood, :photo, :source)'
    );

    $entryIds = [];
    foreach ($entriesData as $e) {
        $insEntry->execute([
            'uid' => $userId,
            'title' => $e['title'],
            'desc' => $e['desc'],
            'date' => $e['date'],
            'cat' => $e['cat'],
            'icon' => $e['icon'],
            'mood' => $e['mood'],
            'photo' => $e['photo'],
            'source' => 'text'
        ]);
        $entryIds[] = (int)$pdo->lastInsertId();
    }

    // Insert saved weekly memory
    $highlights = json_encode(['College project', 'Friends & family', 'Presentation prep', 'Calm walk'], JSON_UNESCAPED_UNICODE);
    $narrative = "This week, you attended lectures and worked on a project, then had lunch with friends after class. You went for an evening walk and recorded feeling Calm. You also prepared a presentation and spent time with family over the weekend.";
    $moodSummary = "1 explicitly recorded mood: Calm, on Oct 7. The other 4 entries have no mood specified.";

    $insMem = $pdo->prepare(
        'INSERT INTO memories (user_id, type, start_date, end_date, date_range_label, title, narrative, highlights, mood_summary, status)
         VALUES (:uid, :type, :start_date, :end_date, :label, :title, :narrative, :highlights, :mood_summary, :status)'
    );
    $insMem->execute([
        'uid' => $userId,
        'type' => 'weekly',
        'start_date' => '2026-10-05',
        'end_date' => '2026-10-11',
        'label' => 'OCT 5–11, 2026',
        'title' => 'A week of progress and connection.',
        'narrative' => $narrative,
        'highlights' => $highlights,
        'mood_summary' => $moodSummary,
        'status' => 'saved'
    ]);
    $memoryId = (int)$pdo->lastInsertId();

    // Link memory to entries
    $linkStmt = $pdo->prepare('INSERT INTO memory_entries (memory_id, entry_id) VALUES (:mid, :eid)');
    foreach ($entryIds as $eid) {
        $linkStmt->execute(['mid' => $memoryId, 'eid' => $eid]);
    }

    // Seed recent searches
    $sStmt = $pdo->prepare('INSERT INTO recent_searches (user_id, query) VALUES (:uid, :q)');
    $sStmt->execute(['uid' => $userId, 'q' => 'project']);
    $sStmt->execute(['uid' => $userId, 'q' => 'College']);

    echo "Demo data seeded successfully for {$fullName} (ID: {$userId})!\n";
} catch (Throwable $e) {
    echo "Seed Error: " . $e->getMessage() . "\n";
}
