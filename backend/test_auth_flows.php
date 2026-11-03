<?php
// Test Auth Flows

function request($url, $method = 'GET', $data = null, $token = null) {
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    $headers = [];
    if ($data !== null) {
        $headers[] = 'Content-Type: application/json';
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    if ($token !== null) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    $response = curl_exec($ch);
    $code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    return ['code' => $code, 'body' => json_decode($response, true) ?: $response];
}

$base = 'http://localhost/echodiary_backend';

echo "=== 1. Signup New User ===\n";
$email = 'autotest_' . time() . '@gmail.com';
$r1 = request("$base/auth/signup.php", 'POST', [
    'full_name' => 'Auto Test User',
    'email' => $email,
    'password' => 'SecurePass123'
]);
echo "Code: {$r1['code']}, Result: " . json_encode($r1['body']) . "\n\n";

echo "=== 2. Duplicate Signup with Same Email ===\n";
$r2 = request("$base/auth/signup.php", 'POST', [
    'full_name' => 'Another User',
    'email' => $email,
    'password' => 'SecurePass123'
]);
echo "Code: {$r2['code']}, Result: " . json_encode($r2['body']) . "\n\n";

echo "=== 3. Login with Correct Credentials ===\n";
$r3 = request("$base/auth/login.php", 'POST', [
    'email' => $email,
    'password' => 'SecurePass123'
]);
echo "Code: {$r3['code']}, Result: " . json_encode($r3['body']) . "\n";
$token = $r3['body']['token'] ?? null;
echo "Token: $token\n\n";

echo "=== 4. Login with Incorrect Password ===\n";
$r4 = request("$base/auth/login.php", 'POST', [
    'email' => $email,
    'password' => 'WrongPassword!'
]);
echo "Code: {$r4['code']}, Result: " . json_encode($r4['body']) . "\n\n";

echo "=== 5. Get Profile with Bearer Token ===\n";
$r5 = request("$base/auth/profiles.php", 'GET', null, $token);
echo "Code: {$r5['code']}, Result: " . json_encode($r5['body']) . "\n\n";

echo "=== 6. Get Profile with Invalid Token ===\n";
$r6 = request("$base/auth/profiles.php", 'GET', null, 'invalidtoken12345678901234567890123456789012345678901234567890123456');
echo "Code: {$r6['code']}, Result: " . json_encode($r6['body']) . "\n\n";

