-- users.login_id UNIQUE
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uq_users_login_id') THEN
    ALTER TABLE users ADD CONSTRAINT uq_users_login_id UNIQUE (login_id);
  END IF;
END $$;

-- refresh_tokens 테이블/컬럼 정합성
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id          BIGSERIAL PRIMARY KEY,
  login_id    VARCHAR(512),
  token_hash  VARCHAR(512) NOT NULL,
  fingerprint VARCHAR(512),
  expires_at  TIMESTAMPTZ  NOT NULL,
  created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
  revoked     BOOLEAN      NOT NULL DEFAULT FALSE
);

DO $$ BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='refresh_tokens' AND column_name='login_id'
  ) THEN
    ALTER TABLE refresh_tokens ADD COLUMN login_id VARCHAR(512);
  END IF;
END $$;

-- 레거시 user_id -> login_id 백필
DO $$ BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name='refresh_tokens' AND column_name='user_id'
  ) THEN
    UPDATE refresh_tokens rt
    SET login_id = u.login_id
    FROM users u
    WHERE rt.user_id = u.uid AND rt.login_id IS NULL;

    ALTER TABLE refresh_tokens DROP COLUMN user_id;
  END IF;
END $$;

-- 인덱스 정리/생성
DO $$ BEGIN
  IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ux_refresh_tokens_token') THEN
    DROP INDEX ux_refresh_tokens_token;
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ux_refresh_tokens_token_hash') THEN
    CREATE UNIQUE INDEX ux_refresh_tokens_token_hash ON refresh_tokens(token_hash);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname='ix_refresh_tokens_login_id') THEN
    CREATE INDEX ix_refresh_tokens_login_id ON refresh_tokens(login_id);
  END IF;
END $$;

-- FK (login_id -> users.login_id)
DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_refresh_tokens_user') THEN
    ALTER TABLE refresh_tokens
      ADD CONSTRAINT fk_refresh_tokens_user
      FOREIGN KEY (login_id) REFERENCES users(login_id) ON DELETE CASCADE;
  END IF;
END $$;

-- 최종 NOT NULL
ALTER TABLE refresh_tokens ALTER COLUMN login_id SET NOT NULL;
