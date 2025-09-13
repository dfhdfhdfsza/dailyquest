-- V2__add_indexes.sql (PostgreSQL) — DailyQuest

-- users
CREATE INDEX IF NOT EXISTS idx_login_id            ON users(login_id);
CREATE INDEX IF NOT EXISTS idx_email               ON users(email);
CREATE INDEX IF NOT EXISTS idx_provider_providerId ON users(provider, provider_id);
CREATE INDEX IF NOT EXISTS idx_users_locked_until  ON users(locked_until);

-- games (unique (uid, gameName) per entity spec — NOTE: original code had {"uid","name"} which seems a typo)
CREATE UNIQUE INDEX IF NOT EXISTS uq_games_uid_gameName ON games(uid, game_name);
CREATE INDEX IF NOT EXISTS idx_uid                        ON games(uid);

-- homeworks
CREATE INDEX IF NOT EXISTS idx_game_id            ON homeworks(game_id);

-- history
CREATE INDEX IF NOT EXISTS idx_user_id            ON history(uid);
CREATE INDEX IF NOT EXISTS idx_homework_id        ON history(homework_id);
CREATE INDEX IF NOT EXISTS idx_start_end          ON history(start_date, end_date);
