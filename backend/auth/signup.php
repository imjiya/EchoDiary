<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Allow: POST');
    respond(405, [
        'success' => false,
        'message' => 'Use POST for signup.'
    ]);
}

$data = getJsonInput();

$fullName = trim((string)($data['full_name'] ?? ''));
$email = strtolower(trim((string)($data['email'] ?? '')));
$password = (string)($data['password'] ?? '');

if ($fullName === '' || $email === '' || $password === '') {
    respond(400, [
        'success' => false,
        'message' => 'Full name, email and password are required.'
    ]);
}

if (strlen($fullName) > 100) {
    respond(400, [
        'success' => false,
        'message' => 'Full name must be 100 characters or fewer.'
    ]);
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 255) {
    respond(400, [
        'success' => false,
        'message' => 'Enter a valid email address.'
    ]);
}

if (strlen($password) < 8 || strlen($password) > 72) {
    respond(400, [
        'success' => false,
        'message' => 'Password must be 8 to 72 bytes long.'
    ]);
}

try {
    $check = $pdo->prepare('SELECT id FROM users WHERE email = :email LIMIT 1');
    $check->execute(['email' => $email]);

    if ($check->fetch()) {
        respond(409, [
            'success' => false,
            'message' => 'An account with this email already exists.'
        ]);
    }

    $passwordHash = password_hash($password, PASSWORD_DEFAULT);

    $pdo->beginTransaction();

    $insert = $pdo->prepare(
        'INSERT INTO users (full_name, email, password_hash)
         VALUES (:full_name, :email, :password_hash)'
    );

    $insert->execute([
        'full_name' => $fullName,
        'email' => $email,
        'password_hash' => $passwordHash
    ]);

    $newUserId = (int)$pdo->lastInsertId();

    // Create default user settings
    $settingsStmt = $pdo->prepare(
        'INSERT INTO user_settings (user_id) VALUES (:user_id)'
    );
    $settingsStmt->execute(['user_id' => $newUserId]);

    // Generate login token automatically on signup
    $token = bin2hex(random_bytes(32));
    $tokenHash = hash('sha256', $token);

    $tokenStmt = $pdo->prepare(
        'INSERT INTO auth_tokens (user_id, token_hash, expires_at)
         VALUES (:user_id, :token_hash, DATE_ADD(NOW(), INTERVAL 30 DAY))'
    );
    $tokenStmt->execute([
        'user_id' => $newUserId,
        'token_hash' => $tokenHash
    ]);

    $pdo->commit();

    respond(201, [
        'success' => true,
        'message' => 'Account created successfully.',
        'token' => $token,
        'user' => [
            'id' => $newUserId,
            'full_name' => $fullName,
            'email' => $email
        ]
    ]);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    if ($e->getCode() === '23000') {
        respond(409, [
            'success' => false,
            'message' => 'An account with this email already exists.'
        ]);
    }

    error_log($e->getMessage());

    respond(500, [
        'success' => false,
        'message' => 'Unable to create account right now.'
    ]);
}
