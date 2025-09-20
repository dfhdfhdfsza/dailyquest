-- ===========================
-- 1) games
-- ===========================
CREATE TABLE IF NOT EXISTS games (
  game_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid         BIGINT      NOT NULL,        -- FK to users(uid)
  game_name   VARCHAR(255) NOT NULL,
  CONSTRAINT fk_games_user FOREIGN KEY (uid) REFERENCES users (uid)
) ENGINE=InnoDB;

-- Unique (user별 게임명 유니크)
CREATE UNIQUE INDEX uq_games_uid_game_name
  ON games (uid, game_name);

-- Indexes
CREATE INDEX idx_uid ON games (uid);

-- ===========================
-- 2) homeworks
-- ===========================
CREATE TABLE IF NOT EXISTS homeworks (
  homework_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
  game_id        BIGINT       NOT NULL,           -- @JoinColumn(name="gameId")
  homework_title VARCHAR(255) NOT NULL,
  homework_memo  VARCHAR(255),
  homework_type  VARCHAR(50)  NOT NULL,
  CONSTRAINT fk_homeworks_game FOREIGN KEY (game_id) REFERENCES games (game_id)
) ENGINE=InnoDB;

-- Indexes
CREATE INDEX idx_game_id ON homeworks (game_id);

-- ===========================
-- 3) history
-- ===========================
CREATE TABLE IF NOT EXISTS history (
  history_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid          BIGINT    NOT NULL,      -- @JoinColumn(name="uid")
  homework_id  BIGINT    NOT NULL,      -- @JoinColumn(name="homeworkId")
  start_date   DATE      NOT NULL,
  end_date     DATE      NOT NULL,
  done         BOOLEAN   NOT NULL,
  CONSTRAINT fk_history_user     FOREIGN KEY (uid) REFERENCES users (uid),
  CONSTRAINT fk_history_homework FOREIGN KEY (homework_id) REFERENCES homeworks (homework_id)
) ENGINE=InnoDB;

-- Indexes
CREATE INDEX idx_user_id     ON history (uid);
CREATE INDEX idx_homework_id ON history (homework_id);
CREATE INDEX idx_start_end   ON history (start_date, end_date);
