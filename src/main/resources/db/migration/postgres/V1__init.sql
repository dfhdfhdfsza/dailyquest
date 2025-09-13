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
  "gameId"     BIGSERIAL PRIMARY KEY,
  uid          BIGINT        NOT NULL,
  "gameName"   VARCHAR(100)  NOT NULL,
  CONSTRAINT fk_games_user FOREIGN KEY(uid) REFERENCES users(uid)
);

-- homeworks
CREATE TABLE IF NOT EXISTS homeworks (
  "homeworkId"     BIGSERIAL PRIMARY KEY,
  "gameId"         BIGINT       NOT NULL,
  "homeworkTitle"  VARCHAR(200) NOT NULL,
  "homeworkMemo"   TEXT         NULL,
  "homeworkType"   VARCHAR(50)  NOT NULL,
  CONSTRAINT fk_homeworks_game FOREIGN KEY("gameId") REFERENCES games("gameId")
);

-- history
CREATE TABLE IF NOT EXISTS history (
  "historyId"  BIGSERIAL PRIMARY KEY,
  uid          BIGINT     NOT NULL,
  "homeworkId" BIGINT     NOT NULL,
  "startDate"  DATE       NOT NULL,
  "endDate"    DATE       NOT NULL,
  done         BOOLEAN    NOT NULL,
  CONSTRAINT fk_history_user     FOREIGN KEY(uid) REFERENCES users(uid),
  CONSTRAINT fk_history_homework FOREIGN KEY("homeworkId") REFERENCES homeworks("homeworkId")
);
