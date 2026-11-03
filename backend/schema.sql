-- EchoDiary Database Schema
-- Compatible with MySQL 5.7+ and MariaDB 10.3+

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS `users` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `profile_image` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Auth Tokens Table
CREATE TABLE IF NOT EXISTS `auth_tokens` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `token_hash` CHAR(64) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_hash` (`token_hash`),
  KEY `idx_auth_user` (`user_id`),
  CONSTRAINT `fk_auth_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Password Reset Tokens Table
CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `token_hash` CHAR(64) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token_hash` (`token_hash`),
  KEY `idx_reset_user` (`user_id`),
  CONSTRAINT `fk_reset_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. User Settings / Preferences Table
CREATE TABLE IF NOT EXISTS `user_settings` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL UNIQUE,
  `appearance` VARCHAR(50) DEFAULT 'Light · calm blue',
  `diary_reminders_enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `weekly_memory_reminders_enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `summary_updates_enabled` TINYINT(1) NOT NULL DEFAULT 0,
  `daily_reminder_time` VARCHAR(20) DEFAULT '8:00 PM',
  `weekly_reminder_time` VARCHAR(50) DEFAULT 'Sunday · 8:00 PM',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_settings_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Categories Lookup Table
CREATE TABLE IF NOT EXISTS `categories` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL UNIQUE,
  `icon` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed default categories
INSERT IGNORE INTO `categories` (`name`, `icon`) VALUES
('College', '🎓'),
('Work', '💼'),
('Friends', '👥'),
('Family', '🏠'),
('Health', '❤️'),
('Travel', '✈️'),
('Exercise', '🧩'),
('Learning', '📖'),
('Hobbies', '🎨'),
('Other', '⋯');

-- 6. Diary Entries Table
CREATE TABLE IF NOT EXISTS `diary_entries` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `transcript` TEXT DEFAULT NULL,
  `entry_date` DATE NOT NULL,
  `category` VARCHAR(50) NOT NULL DEFAULT 'Other',
  `category_icon` VARCHAR(10) NOT NULL DEFAULT '📝',
  `mood` VARCHAR(50) NOT NULL DEFAULT 'Not specified',
  `photo_url` VARCHAR(255) DEFAULT NULL,
  `audio_url` VARCHAR(255) DEFAULT NULL,
  `source_type` ENUM('text', 'voice') NOT NULL DEFAULT 'text',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_entries` (`user_id`, `entry_date`),
  KEY `idx_category` (`category`),
  KEY `idx_mood` (`mood`),
  CONSTRAINT `fk_entries_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Memories Table
CREATE TABLE IF NOT EXISTS `memories` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `type` ENUM('weekly', 'monthly', 'yearly') NOT NULL DEFAULT 'weekly',
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `date_range_label` VARCHAR(100) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `narrative` TEXT NOT NULL,
  `highlights` LONGTEXT DEFAULT NULL,
  `mood_summary` TEXT DEFAULT NULL,
  `status` ENUM('draft', 'saved') NOT NULL DEFAULT 'saved',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_memories` (`user_id`, `type`, `start_date`),
  CONSTRAINT `fk_memories_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Memory Entries Pivot Table
CREATE TABLE IF NOT EXISTS `memory_entries` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `memory_id` INT(10) UNSIGNED NOT NULL,
  `entry_id` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_memory_entry` (`memory_id`, `entry_id`),
  CONSTRAINT `fk_pivot_memory` FOREIGN KEY (`memory_id`) REFERENCES `memories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_pivot_entry` FOREIGN KEY (`entry_id`) REFERENCES `diary_entries` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. Recent Searches Table
CREATE TABLE IF NOT EXISTS `recent_searches` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT(10) UNSIGNED NOT NULL,
  `query` VARCHAR(100) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_recent_search` (`user_id`, `created_at`),
  CONSTRAINT `fk_searches_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

