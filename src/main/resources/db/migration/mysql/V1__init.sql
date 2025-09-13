-- V1__init.sql (MySQL) — DailyQuest
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- users
CREATE TABLE IF NOT EXISTS users (
  uid             BIGINT PRIMARY KEY AUTO_INCREMENT,
  username        VARCHAR(20)   NOT NULL,
  login_id        VARCHAR(30),
  password        VARCHAR(60),
  email           VARCHAR(30),
  role            VARCHAR(20)   NOT NULL,
  provider        VARCHAR(30),
  provider_id     VARCHAR(191),
  failed_attempts INT           NOT NULL DEFAULT 0,
  last_failed_at  DATETIME(6)   NULL,
  locked_until    DATETIME(6)   NULL
) ENGINE=InnoDB;

-- games
CREATE TABLE IF NOT EXISTS games (
  gameId     BIGINT PRIMARY KEY AUTO_INCREMENT,
  uid        BIGINT      NOT NULL,
  gameName   VARCHAR(100) NOT NULL,
  CONSTRAINT fk_games_user FOREIGN KEY(uid) REFERENCES users(uid)
) ENGINE=InnoDB;

-- homeworks
CREATE TABLE IF NOT EXISTS homeworks (
  homeworkId     BIGINT PRIMARY KEY AUTO_INCREMENT,
  gameId         BIGINT       NOT NULL,
  homeworkTitle  VARCHAR(200) NOT NULL,
  homeworkMemo   TEXT         NULL,
  homeworkType   VARCHAR(50)  NOT NULL,
  CONSTRAINT fk_homeworks_game FOREIGN KEY(gameId) REFERENCES games(gameId)
) ENGINE=InnoDB;

-- history
CREATE TABLE IF NOT EXISTS history (
  historyId   BIGINT PRIMARY KEY AUTO_INCREMENT,
  uid         BIGINT     NOT NULL,
  homeworkId  BIGINT     NOT NULL,
  startDate   DATE       NOT NULL,
  endDate     DATE       NOT NULL,
  done        TINYINT(1) NOT NULL,
  CONSTRAINT fk_history_user     FOREIGN KEY(uid) REFERENCES users(uid),
  CONSTRAINT fk_history_homework FOREIGN KEY(homeworkId) REFERENCES homeworks(homeworkId)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS=1;
