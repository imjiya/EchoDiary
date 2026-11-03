<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    header('Allow: GET');
    respond(405, ['success' => false, 'message' => 'Use GET for user settings.']);
}

$userId = getAuthenticatedUserId($pdo);

try {
    // Fetch or create user settings
    $stmt = $pdo->prepare('SELECT * FROM user_settings WHERE user_id = :user_id LIMIT 1');
    $stmt->execute(['user_id' => $userId]);
    $settings = $stmt->fetch();

    if (!$settings) {
        $ins = $pdo->prepare('INSERT INTO user_settings (user_id) VALUES (:user_id)');
        $ins->execute(['user_id' => $userId]);
        $stmt->execute(['user_id' => $userId]);
        $settings = $stmt->fetch();
    }

    // Fetch user name
    $uStmt = $pdo->prepare('SELECT full_name FROM users WHERE id = :id');
    $uStmt->execute(['id' => $userId]);
    $fullName = (string)$uStmt->fetchColumn() ?: 'User';

    // Count entries and saved memories for storage summary
    $entryCountStmt = $pdo->prepare('SELECT COUNT(*) FROM diary_entries WHERE user_id = :id');
    $entryCountStmt->execute(['id' => $userId]);
    $entriesCount = (int)$entryCountStmt->fetchColumn();

    $memCountStmt = $pdo->prepare("SELECT COUNT(*) FROM memories WHERE user_id = :id AND status = 'saved'");
    $memCountStmt->execute(['id' => $userId]);
    $memoriesCount = (int)$memCountStmt->fetchColumn();

    $storageSummary = "{$entriesCount} entries · {$memoriesCount} saved memory";

    respond(200, [
        'success' => true,
        'user_name' => $fullName,
        'appearance_summary' => $settings['appearance'] ?? 'Light · calm blue',
        'storage_summary' => $storageSummary,
        'diary_entries_count' => $entriesCount,
        'saved_memories_count' => $memoriesCount,
        'notifications' => [
            'diary_reminders_enabled' => (bool)$settings['diary_reminders_enabled'],
            'weekly_memory_reminders_enabled' => (bool)$settings['weekly_memory_reminders_enabled'],
            'summary_updates_enabled' => (bool)$settings['summary_updates_enabled'],
            'daily_reminder_time' => $settings['daily_reminder_time'],
            'weekly_reminder_time' => $settings['weekly_reminder_time']
        ]
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to load settings.']);
}

