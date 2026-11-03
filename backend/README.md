# EchoDiary Backend

PHP + MySQL REST API backend for the **EchoDiary AI** Android mobile application.

## 🚀 Features

- **Authentication**: Secure registration, login with 64-character SHA-256 hashed bearer tokens, password resets, and user profile management.
- **Diary Entries**: Create, update, list, filter, search, and delete entries with support for categories, moods, photo uploads, audio recordings, and voice transcripts.
- **Memories & AI Summaries**: Grounded weekly, monthly, and yearly summaries with mood evidence and highlight tracking.
- **Calendar & Analytics**: Month overviews, day-specific entries, home dashboard summary, and activity/mood breakdowns.
- **User Settings**: Notification schedules, appearance preferences, and storage tracking.

---

## 🛠️ Database Setup

1. Start Apache and MySQL via XAMPP.
2. Create the database in MySQL:
   ```sql
   CREATE DATABASE echo_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Import the schema file:
   ```bash
   mysql -u root echo_diary < schema.sql
   ```
4. (Optional) Pre-seed demo entries:
   ```bash
   php seed_demo_data.php
   ```

---

## 📡 API Endpoints

### Base URL
- **Local machine**: `http://localhost/echodiary_backend/`
- **Android Emulator**: `http://10.0.2.2/echodiary_backend/`

### 1. Authentication (`auth/`)
- `POST auth/signup.php` — Register new user
- `POST auth/login.php` — Log in and receive 64-character Bearer token
- `GET auth/profiles.php` — Get current profile, statistics, and initials
- `POST/PUT auth/profiles.php` — Update profile details and profile picture
- `POST auth/forgotpd.php` — Request password reset token
- `POST auth/resetpasswd.php` — Reset password using token

### 2. Diary Entries (`entries/`)
- `POST entries/create.php` — Create entry (text/voice, photo/audio)
- `GET entries/list.php` — List entries with query, category, mood, and date filters
- `GET entries/detail.php?id={id}` — Entry details and associated memory badge
- `POST/PUT entries/update.php` — Update diary entry
- `POST/DELETE entries/delete.php` — Delete diary entry
- `GET/DELETE entries/search.php` — Search entries and manage recent searches

### 3. Memories & AI Summaries (`memories/`)
- `GET memories/list.php?type={weekly|monthly|yearly}` — List saved memories
- `GET memories/detail.php?id={id}` — Memory detail with linked source entries
- `POST/GET memories/generate.php` — Generate dynamic weekly/monthly memory draft
- `POST/PUT memories/update.php` — Edit memory title and narrative
- `GET memories/photos.php` — Feed of entries with attached photos

### 4. Calendar & Analytics (`calendar/`, `analytics/`)
- `GET calendar/month_overview.php?year=YYYY&month=MM` — Month overview with entry dots
- `GET calendar/day_entries.php?date=YYYY-MM-DD` — Entries for specific day
- `GET analytics/dashboard.php` — Realtime dashboard summary for home screen
- `GET analytics/insights.php?tab={Week|Month|Year}` — Category & mood analytics

### 5. Settings & Categories (`settings/`, `categories/`)
- `GET settings/get.php` — Fetch user settings & reminders
- `POST/PUT settings/update.php` — Update user preferences
- `GET categories/list.php` — List standard categories with icons
