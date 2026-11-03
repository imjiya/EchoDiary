<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';

$userId = getAuthenticatedUserId($pdo);
$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    try {
        $stmt = $pdo->prepare(
            'SELECT id, full_name, email, profile_image, created_at FROM users WHERE id = :id LIMIT 1'
        );
        $stmt->execute(['id' => $userId]);
        $user = $stmt->fetch();

        if (!$user) {
            respond(404, ['success' => false, 'message' => 'User not found.']);
        }

        // Count total diary entries
        $entryStmt = $pdo->prepare('SELECT COUNT(*) FROM diary_entries WHERE user_id = :id');
        $entryStmt->execute(['id' => $userId]);
        $entryCount = (int)$entryStmt->fetchColumn();

        // Count total saved memories
        $memoryStmt = $pdo->prepare("SELECT COUNT(*) FROM memories WHERE user_id = :id AND status = 'saved'");
        $memoryStmt->execute(['id' => $userId]);
        $memoryCount = (int)$memoryStmt->fetchColumn();

        // Compute initials
        $nameParts = preg_split('/\s+/', trim((string)$user['full_name']));
        $initials = 'AM';
        if (!empty($nameParts[0])) {
            if (count($nameParts) > 1 && !empty($nameParts[1])) {
                $initials = strtoupper(mb_substr($nameParts[0], 0, 1) . mb_substr($nameParts[1], 0, 1));
            } else {
                $initials = strtoupper(mb_substr($nameParts[0], 0, 2));
            }
        }

        respond(200, [
            'success' => true,
            'message' => 'Profile retrieved successfully.',
            'profile' => [
                'id' => (int)$user['id'],
                'full_name' => $user['full_name'],
                'email' => $user['email'],
                'profile_image' => $user['profile_image'] ? getBaseUrl() . '/' . $user['profile_image'] : null,
                'initials' => $initials,
                'diary_entries_count' => $entryCount,
                'saved_memories_count' => $memoryCount,
                'created_at' => $user['created_at']
            ]
        ]);
    } catch (Throwable $e) {
        error_log($e->getMessage());
        respond(500, ['success' => false, 'message' => 'Unable to retrieve profile.']);
    }
} elseif ($method === 'POST' || $method === 'PUT') {
    $data = getJsonInput();
    $fullName = trim((string)($data['full_name'] ?? ''));
    $email = strtolower(trim((string)($data['email'] ?? '')));

    if ($fullName === '' && $email === '' && !isset($_FILES['profile_image'])) {
        respond(400, ['success' => false, 'message' => 'No fields provided to update.']);
    }

    try {
        $updates = [];
        $params = ['id' => $userId];

        if ($fullName !== '') {
            $updates[] = 'full_name = :full_name';
            $params['full_name'] = $fullName;
        }

        if ($email !== '') {
            if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                respond(400, ['success' => false, 'message' => 'Invalid email address format.']);
            }
            // Check email uniqueness
            $check = $pdo->prepare('SELECT id FROM users WHERE email = :email AND id != :id LIMIT 1');
            $check->execute(['email' => $email, 'id' => $userId]);
            if ($check->fetch()) {
                respond(409, ['success' => false, 'message' => 'This email is already in use by another account.']);
            }
            $updates[] = 'email = :email';
            $params['email'] = $email;
        }

        // Handle profile image upload
        if (isset($_FILES['profile_image']) && $_FILES['profile_image']['error'] === UPLOAD_ERR_OK) {
            $file = $_FILES['profile_image'];
            $ext = strtolower(pathinfo($file['name'], PATHINFO_EXTENSION));
            $allowed = ['jpg', 'jpeg', 'png', 'webp', 'gif'];
            if (!in_array($ext, $allowed, true)) {
                respond(400, ['success' => false, 'message' => 'Invalid image format. Supported: jpg, png, webp.']);
            }
            $fileName = 'profile_' . $userId . '_' . time() . '.' . $ext;
            $dest = __DIR__ . '/../uploads/' . $fileName;
            if (move_uploaded_file($file['tmp_name'], $dest)) {
                $updates[] = 'profile_image = :profile_image';
                $params['profile_image'] = 'uploads/' . $fileName;
            }
        }

        if (empty($updates)) {
            respond(400, ['success' => false, 'message' => 'Nothing to update.']);
        }

        $sql = 'UPDATE users SET ' . implode(', ', $updates) . ' WHERE id = :id';
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);

        // Fetch updated
        $stmt = $pdo->prepare('SELECT id, full_name, email, profile_image FROM users WHERE id = :id');
        $stmt->execute(['id' => $userId]);
        $user = $stmt->fetch();

        respond(200, [
            'success' => true,
            'message' => 'Profile updated successfully.',
            'profile' => [
                'id' => (int)$user['id'],
                'full_name' => $user['full_name'],
                'email' => $user['email'],
                'profile_image' => $user['profile_image'] ? getBaseUrl() . '/' . $user['profile_image'] : null
            ]
        ]);
    } catch (Throwable $e) {
        error_log($e->getMessage());
        respond(500, ['success' => false, 'message' => 'Unable to update profile.']);
    }
} else {
    header('Allow: GET, POST, PUT');
    respond(405, ['success' => false, 'message' => 'Method not allowed.']);
}
