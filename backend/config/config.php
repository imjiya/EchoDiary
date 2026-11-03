<?php
declare(strict_types=1);

// Enable CORS for mobile app & local dev
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=utf-8');

if (($_SERVER['REQUEST_METHOD'] ?? '') === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$host = 'localhost';
$dbname = 'echo_diary';
$dbuser = 'root';
$dbpass = ''; // Default XAMPP password

try {
    $pdo = new PDO(
        "mysql:host=$host;dbname=$dbname;charset=utf8mb4",
        $dbuser,
        $dbpass,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false
        ]
    );
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Database connection failed: ' . $e->getMessage()
    ]);
    exit;
}

/**
 * Send JSON response and terminate execution.
 */
function respond(int $status, array $data): never
{
    http_response_code($status);
    echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

/**
 * Retrieve JSON body or multipart form fields.
 */
function getJsonInput(): array
{
    $raw = file_get_contents('php://input');
    if (empty($raw)) {
        return $_POST;
    }
    $data = json_decode($raw, true);

    if (!is_array($data)) {
        return $_POST;
    }

    return array_merge($_POST, $data);
}

/**
 * Authenticate request via Bearer token and return user ID.
 */
function getAuthenticatedUserId(PDO $pdo): int
{
    $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? '';

    if (empty($authHeader) && function_exists('getallheaders')) {
        foreach (getallheaders() as $name => $value) {
            if (strtolower($name) === 'authorization') {
                $authHeader = $value;
                break;
            }
        }
    }

    if (!preg_match('/^Bearer\s+([a-f0-9]{64})$/i', trim($authHeader), $matches)) {
        respond(401, [
            'success' => false,
            'message' => 'Missing or invalid authorization token.'
        ]);
    }

    $tokenHash = hash('sha256', $matches[1]);

    try {
        $stmt = $pdo->prepare(
            'SELECT user_id FROM auth_tokens WHERE token_hash = :token_hash AND expires_at > NOW() LIMIT 1'
        );
        $stmt->execute(['token_hash' => $tokenHash]);
        $row = $stmt->fetch();

        if (!$row) {
            respond(401, [
                'success' => false,
                'message' => 'Session expired or invalid. Please log in again.'
            ]);
        }

        return (int)$row['user_id'];
    } catch (PDOException $e) {
        error_log($e->getMessage());
        respond(500, [
            'success' => false,
            'message' => 'Authentication query error.'
        ]);
    }
}

/**
 * Base URL helper for serving uploaded photos and audio files.
 */
function getBaseUrl(): string
{
    $scheme = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on') ? 'https' : 'http';
    $host = $_SERVER['HTTP_HOST'] ?? 'localhost';
    return "{$scheme}://{$host}/echodiary_backend";
}
