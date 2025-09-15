-- Vx__init_core_tables.sql
-- PostgreSQL

-- 1) users --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    uid              BIGSERIAL PRIMARY KEY,
    login_id         VARCHAR(191)  NOT NULL,
    password         VARCHAR(255)  NOT NULL,
    email            VARCHAR(255),
    nickname         VARCHAR(64),
    role             VARCHAR(20)   NOT NULL,
    provider         VARCHAR(64),
    provider_id      VARCHAR(128),
    failed_attempts  INTEGER       NOT NULL DEFAULT 0,
    last_failed_at   TIMESTAMPTZ,
    locked_until     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ
);

-- FK에서 참조할 수 있도록 login_id 는 UNIQUE 여야 함
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_login_id ON users (login_id);
-- 필요시 이메일도 유니크로 쓰고 싶다면 UNIQUE 로 바꿔도 됨
CREATE INDEX        IF NOT EXISTS idx_users_email                 ON users (email);
CREATE INDEX        IF NOT EXISTS idx_users_provider_provider_id  ON users (provider, provider_id);

--------------------------------------------------------------------------

-- 2) games --------------------------------------------------------------
CREATE TABLE IF NOT EXISTS games (
    game_id     BIGSERIAL PRIMARY KEY,
    uid         BIGINT      NOT NULL,
    game_name   VARCHAR(255) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

ALTER TABLE games
    ADD CONSTRAINT fk_games_user
    FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE;

-- 한 유저 안에서 게임 이름 중복 방지
CREATE UNIQUE INDEX IF NOT EXISTS ux_games_user_name ON games (uid, game_name);
CREATE INDEX        IF NOT EXISTS idx_games_uid      ON games (uid);

--------------------------------------------------------------------------

-- 3) homeworks ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS homeworks (
    homework_id BIGSERIAL PRIMARY KEY,
    game_id     BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

ALTER TABLE homeworks
    ADD CONSTRAINT fk_homeworks_game
    FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE;

CREATE INDEX        IF NOT EXISTS idx_homeworks_game_id   ON homeworks (game_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_homeworks_game_name  ON homeworks (game_id, name);

--------------------------------------------------------------------------

-- 4) history ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS history (
    history_id  BIGSERIAL PRIMARY KEY,
    homework_id BIGINT       NOT NULL,
    start_date  TIMESTAMPTZ  NOT NULL,
    end_date    TIMESTAMPTZ,
    done        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

ALTER TABLE history
    ADD CONSTRAINT fk_history_homework
    FOREIGN KEY (homework_id) REFERENCES homeworks(homework_id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_history_homework_id ON history (homework_id);

--------------------------------------------------------------------------

-- 5) refresh_tokens -----------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    login_id     VARCHAR(191)  NOT NULL,      -- users.login_id 를 FK로 참조
    token_hash   VARCHAR(512)  NOT NULL,      -- 토큰 원문은 저장하지 말고 해시 권장
    fingerprint  VARCHAR(512),
    expires_at   TIMESTAMPTZ   NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    persistent   BOOLEAN       NOT NULL DEFAULT FALSE,
    revoked      BOOLEAN       NOT NULL DEFAULT FALSE
);

-- users.login_id 가 UNIQUE 이어야 FK 가능 (위에서 ux_users_login_id 생성)
ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (login_id) REFERENCES users(login_id) ON DELETE CASCADE;

-- 조회 성능 및 중복 방지를 위한 인덱스들
CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token_hash        ON refresh_tokens (token_hash);
CREATE INDEX        IF NOT EXISTS idx_refresh_tokens_expires_at       ON refresh_tokens (expires_at);
CREATE INDEX        IF NOT EXISTS idx_refresh_tokens_login_fingerprint ON refresh_tokens (login_id, fingerprint);



