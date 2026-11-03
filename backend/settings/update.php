<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'PUT') {
    header('Allow: POST, PUT');
    respond(405, ['success' => false, 'message' => 'Use POST or PUT to update settings.']);
}

$userId = getAuthenticatedUserId($pdo);
$data = getJsonInput();

$allowedFields = [
    'appearance',
    'diary_reminders_enabled',
    'weekly_memory_reminders_enabled',
    'summary_updates_enabled',
    'daily_reminder_time',
    'weekly_reminder_time'
];

$updates = [];
$params = ['user_id' => $userId];

foreach ($allowedFields as $f) {
    if (isset($data[$f])) {
        $val = $data[$f];
        if (str_contains($f, '_enabled')) {
            $val = filter_var($val, FILTER_VALIDATE_BOOLEAN) ? 1 : 0;
        }
        $updates[] = "{$f} = :{$f}";
        $params[$f] = $val;
    }
}

if (empty($updates)) {
    respond(400, ['success' => false, 'message' => 'No settings provided to update.']);
}

try {
    // Ensure row exists
    $pdo->prepare('INSERT IGNORE INTO user_settings (user_id) VALUES (:user_id)')->execute(['user_id' => $userId]);

    $sql = 'UPDATE user_settings SET ' . implode(', ', $updates) . ' WHERE user_id = :user_id';
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);

    respond(200, [
        'success' => true,
        'message' => 'Settings updated successfully.'
    ]);
} catch (Throwable $e) {
    error_log($e->getMessage());
    respond(500, ['success' => false, 'message' => 'Failed to update settings.']);
}

