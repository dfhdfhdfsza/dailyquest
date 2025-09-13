-- db/migration/postgres/V3__create_refresh_tokens.sql  (PostgreSQL)

-- 1) 테이블이 없으면 올바른 스키마로 생성
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,                 -- FK → users(uid)
    token_hash   VARCHAR(512) NOT NULL,
    fingerprint  VARCHAR(512),
    expires_at   TIMESTAMPTZ NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked      BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2) 과거 스키마( login_id )가 존재한다면 user_id로 마이그레이션
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_name = 'refresh_tokens' AND column_name = 'login_id'
  ) THEN
    -- user_id 없으면 추가
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_name = 'refresh_tokens' AND column_name = 'user_id'
    ) THEN
      ALTER TABLE refresh_tokens ADD COLUMN user_id BIGINT;
    END IF;

    -- login_id → user_id 매핑 (users.login_id → users.uid)
    UPDATE refresh_tokens rt
    SET user_id = u.uid
    FROM users u
    WHERE u.login_id = rt.login_id
      AND rt.user_id IS NULL;

    -- user_id NOT NULL 강제
    ALTER TABLE refresh_tokens ALTER COLUMN user_id SET NOT NULL;

    -- 더 이상 쓰지 않는 login_id 제거
    ALTER TABLE refresh_tokens DROP COLUMN login_id;
  END IF;
END $$;

-- 3) 인덱스 정리/생성 (이전 잘못된 이름들도 정리)
DO $$
BEGIN
  -- 잘못 만든 인덱스들 제거 시도 (있으면만)
  IF EXISTS (SELECT 1 FROM pg_indexes WHERE schemaname = 'public' AND indexname = 'ux_refresh_tokens_token') THEN
    EXECUTE 'DROP INDEX public.ux_refresh_tokens_token';
  END IF;
  IF EXISTS (SELECT 1 FROM pg_indexes WHERE schemaname = 'public' AND indexname = 'ix_refresh_tokens_user') THEN
    EXECUTE 'DROP INDEX public.ix_refresh_tokens_user';
  END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE        INDEX IF NOT EXISTS ix_refresh_tokens_user_id   ON refresh_tokens (user_id);

-- 4) FK: refresh_tokens.user_id → users(uid)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.table_constraints
    WHERE table_name = 'refresh_tokens'
      AND constraint_name = 'fk_refresh_tokens_user'
      AND constraint_type = 'FOREIGN KEY'
  ) THEN
    ALTER TABLE refresh_tokens
      ADD CONSTRAINT fk_refresh_tokens_user
      FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE;
  END IF;
END $$;
