
<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, [
        'success' => false,
        'message' => 'Use POST for password reset.'
    ]);
}

$data = getJsonInput();

$email = strtolower(trim((string)($data['email'] ?? '')));
$token = trim((string)($data['token'] ?? ''));
$newPassword = (string)($data['new_password'] ?? '');

if (
    !filter_var($email, FILTER_VALIDATE_EMAIL) ||
    strlen($token) !== 64 ||
    strlen($newPassword) < 8 ||
    strlen($newPassword) > 72
) {
    respond(400, [
        'success' => false,
        'message' =>
            'Enter a valid email, reset token and password of 8 to 72 bytes.'
    ]);
}

try {
    $tokenHash = hash('sha256', $token);

    $stmt = $pdo->prepare(
        'SELECT r.id AS reset_id, r.user_id
         FROM password_reset_tokens r
         JOIN users u ON u.id = r.user_id
         WHERE u.email = :email
           AND r.token_hash = :token_hash
           AND r.expires_at > NOW()
         LIMIT 1'
    );

    $stmt->execute([
        'email' => $email,
        'token_hash' => $tokenHash
    ]);

    $reset = $stmt->fetch();

    if (!$reset) {
        respond(400, [
            'success' => false,
            'message' => 'Invalid or expired reset token.'
        ]);
    }

    $passwordHash = password_hash(
        $newPassword,
        PASSWORD_DEFAULT
    );

    $pdo->beginTransaction();

    $update = $pdo->prepare(
        'UPDATE users
         SET password_hash = :password_hash
         WHERE id = :user_id'
    );

    $update->execute([
        'password_hash' => $passwordHash,
        'user_id' => $reset['user_id']
    ]);

    // Delete all reset tokens for this user so they cannot be reused.
    $delete = $pdo->prepare(
        'DELETE FROM password_reset_tokens WHERE user_id = :user_id'
    );
    $delete->execute(['user_id' => $reset['user_id']]);

    $pdo->commit();

    respond(200, [
        'success' => true,
        'message' => 'Password reset successful. Please log in.'
    ]);

} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    error_log($e->getMessage());

    respond(500, [
        'success' => false,
        'message' => 'Unable to reset password right now.'
    ]);
}
