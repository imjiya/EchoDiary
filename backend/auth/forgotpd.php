
<?php


require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, [
        'success' => false,
        'message' => 'Use POST for forgot password.'
    ]);
}

$data = getJsonInput();
$email = strtolower(trim((string)($data['email'] ?? '')));

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    respond(400, [
        'success' => false,
        'message' => 'Enter a valid email address.'
    ]);
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, email FROM users WHERE email = :email LIMIT 1'
    );
    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch();

    if ($user) {
        // Generate a secure, random reset token.
        $token = bin2hex(random_bytes(32));
        $tokenHash = hash('sha256', $token);

        // Invalidate previous reset tokens for this user.
        $delete = $pdo->prepare(
            'DELETE FROM password_reset_tokens WHERE user_id = :user_id'
        );
        $delete->execute(['user_id' => $user['id']]);

        // Token expires in 30 minutes.
        $insert = $pdo->prepare(
            'INSERT INTO password_reset_tokens
                (user_id, token_hash, expires_at)
             VALUES
                (:user_id, :token_hash, DATE_ADD(NOW(), INTERVAL 30 MINUTE))'
        );

        $insert->execute([
            'user_id' => $user['id'],
            'token_hash' => $tokenHash
        ]);

        // Configure your email sender before using this in production.
        $subject = 'EchoDiary AI - Password Reset';
        $message =
            "Your EchoDiary AI password reset token is:\n\n" .
            $token .
            "\n\nThis token expires in 30 minutes. " .
            "If you did not request this, ignore this email.";

        $headers = "From: EchoDiary AI <no-reply@example.com>";

        if (!mail($user['email'], $subject, $message, $headers)) {
            error_log('Password reset email could not be sent.');
        }
    }

    // Do not reveal whether an email address has an account.
    respond(200, [
        'success' => true,
        'message' =>
            'If an account exists for that email, password reset instructions will be sent.'
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    respond(500, [
        'success' => false,
        'message' => 'Unable to process the request right now.'
    ]);
}
