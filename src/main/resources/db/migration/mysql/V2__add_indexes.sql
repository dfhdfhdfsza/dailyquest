-- V2__add_indexes.sql (MySQL) — DailyQuest

-- users
CREATE INDEX idx_login_id                   ON users(login_id);
CREATE INDEX idx_email                      ON users(email);
CREATE INDEX idx_provider_providerId        ON users(provider, provider_id);
CREATE INDEX idx_users_locked_until         ON users(locked_until);

-- games (unique (uid, gameName) per entity spec — NOTE: original code had {"uid","name"} which seems a typo)
CREATE UNIQUE INDEX uq_games_uid_gameName   ON games(uid, gameName);
CREATE INDEX idx_uid                        ON games(uid);

-- homeworks
CREATE INDEX idx_game_id                    ON homeworks(gameId);

-- history
CREATE INDEX idx_user_id                    ON history(uid);
CREATE INDEX idx_homework_id                ON history(homeworkId);
CREATE INDEX idx_start_end                  ON history(startDate, endDate);
