
<?php


require_once __DIR__ . '/../config/config.php';

// Only allow POST requests.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, [
        'success' => false,
        'message' => 'Use POST for login.'
    ]);
}

// Read JSON input.
$data = getJsonInput();

$email = strtolower(trim((string)($data['email'] ?? '')));
$password = (string)($data['password'] ?? '');

// Validate required fields.
if ($email === '' || $password === '') {
    respond(400, [
        'success' => false,
        'message' => 'Email and password are required.'
    ]);
}

if (
    !filter_var($email, FILTER_VALIDATE_EMAIL) ||
    strlen($email) > 255
) {
    respond(401, [
        'success' => false,
        'message' => 'Invalid email or password.'
    ]);
}

try {
    // Find the user by email.
    $stmt = $pdo->prepare(
        'SELECT id, full_name, email, password_hash
         FROM users
         WHERE email = :email
         LIMIT 1'
    );

    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch();

    // Verify the password.
    if (
        !$user ||
        !password_verify($password, $user['password_hash'])
    ) {
        respond(401, [
            'success' => false,
            'message' => 'Invalid email or password.'
        ]);
    }

    // Generate a secure, random login token.
    $token = bin2hex(random_bytes(32));
    $tokenHash = hash('sha256', $token);

    // Save only the token hash in the database.
    // The login session expires after 7 days.
    $tokenStmt = $pdo->prepare(
        'INSERT INTO auth_tokens
            (user_id, token_hash, expires_at)
         VALUES
            (:user_id, :token_hash,
             DATE_ADD(NOW(), INTERVAL 7 DAY))'
    );

    $tokenStmt->execute([
        'user_id' => $user['id'],
        'token_hash' => $tokenHash
    ]);

    // Return the original token to the app.
    respond(200, [
        'success' => true,
        'message' => 'Login successful.',
        'token' => $token,
        'user' => [
            'id' => (int)$user['id'],
            'full_name' => $user['full_name'],
            'email' => $user['email']
        ]
    ]);

} catch (PDOException $e) {
    error_log($e->getMessage());

    respond(500, [
        'success' => false,
        'message' => 'Unable to log in right now.'
    ]);

} catch (Throwable $e) {
    error_log($e->getMessage());

    respond(500, [
        'success' => false,
        'message' => 'An unexpected error occurred.'
    ]);
}
