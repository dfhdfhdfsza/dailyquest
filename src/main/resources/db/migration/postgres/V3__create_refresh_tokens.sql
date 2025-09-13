-- V3__create_refresh_tokens.sql

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    login_id     VARCHAR(512) NOT NULL,          -- users 테이블 PK 타입에 맞춰서
    token_hash   VARCHAR(512) NOT NULL,
    fingerprint  VARCHAR(512),
    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    revoked      BOOLEAN NOT NULL DEFAULT FALSE
);

-- 유니크/조회 인덱스 (필요 시)
CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX        IF NOT EXISTS ix_refresh_tokens_user  ON refresh_tokens (user_id);

-- 외래키 (users 테이블 명/PK 컬럼명에 맞게 수정)
ALTER TABLE refresh_tokens
ADD CONSTRAINT fk_refresh_tokens_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
