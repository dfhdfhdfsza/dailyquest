-- V1__init.sql (PostgreSQL) — DailyQuest

-- users
CREATE TABLE IF NOT EXISTS users (
  uid             BIGSERIAL PRIMARY KEY,
  username        VARCHAR(20)    NOT NULL,
  login_id        VARCHAR(30),
  password        VARCHAR(60),
  email           VARCHAR(30),
  role            VARCHAR(20)    NOT NULL,
  provider        VARCHAR(30),
  provider_id     VARCHAR(191),
  failed_attempts INTEGER        NOT NULL DEFAULT 0,
  last_failed_at  TIMESTAMPTZ    NULL,
  locked_until    TIMESTAMPTZ    NULL
);

-- games
CREATE TABLE IF NOT EXISTS games (
  game_id     BIGSERIAL PRIMARY KEY,
  uid          BIGINT        NOT NULL,
  game_name   VARCHAR(100)  NOT NULL,
  CONSTRAINT fk_games_user FOREIGN KEY(uid) REFERENCES users(uid)
);

-- homeworks
CREATE TABLE IF NOT EXISTS homeworks (
  homework_id     BIGSERIAL PRIMARY KEY,
  game_id         BIGINT       NOT NULL,
  homework_title  VARCHAR(200) NOT NULL,
  homework_memo   TEXT         NULL,
  homework_type   VARCHAR(50)  NOT NULL,
  CONSTRAINT fk_homeworks_game FOREIGN KEY(game_id) REFERENCES games(game_id)
);

-- history
CREATE TABLE IF NOT EXISTS history (
  history_id  BIGSERIAL PRIMARY KEY,
  uid          BIGINT     NOT NULL,
  homework_id BIGINT     NOT NULL,
  start_date  DATE       NOT NULL,
  end_date    DATE       NOT NULL,
  done         BOOLEAN    NOT NULL,
  CONSTRAINT fk_history_user     FOREIGN KEY(uid) REFERENCES users(uid),
  CONSTRAINT fk_history_homework FOREIGN KEY(homework_id) REFERENCES homeworks(homework_id)
);
