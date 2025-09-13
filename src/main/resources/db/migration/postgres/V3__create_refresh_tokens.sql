-- V3__create_refresh_tokens.sql  (PostgreSQL)

-- 1) 테이블 생성
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    login_id     VARCHAR(512) NOT NULL,                 -- users PK와 타입 맞춤
    token_hash   VARCHAR(512) NOT NULL,
    fingerprint  VARCHAR(512),
    expires_at   TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked      BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2) 인덱스: 컬럼명에 맞게 수정 (token -> token_hash, user_id -> login_id)
CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE        INDEX IF NOT EXISTS ix_refresh_tokens_login_id   ON refresh_tokens (login_id);

-- 3) 외래키: login_id -> users(login_id) 로 연결 (테이블/컬럼명 다르면 아래 두 곳만 바꿔줘)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'refresh_tokens'
          AND constraint_type = 'FOREIGN KEY'
          AND constraint_name = 'fk_refresh_tokens_user'
    ) THEN
        ALTER TABLE refresh_tokens
            ADD CONSTRAINT fk_refresh_tokens_user
            FOREIGN KEY (login_id) REFERENCES users(login_id) ON DELETE CASCADE;
    END IF;
END $$;
