DO $$
BEGIN
  -- 1) refresh_tokens.login_id 컬럼 없으면 추가
  IF NOT EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_name = 'refresh_tokens'
        AND column_name = 'login_id'
  ) THEN
    ALTER TABLE refresh_tokens
      ADD COLUMN login_id VARCHAR(191);
  END IF;

  -- 2) users.login_id 유니크 보장
  IF NOT EXISTS (
      SELECT 1 FROM information_schema.table_constraints
      WHERE table_name = 'users'
        AND constraint_type = 'UNIQUE'
        AND constraint_name = 'uq_users_login_id'
  ) THEN
    ALTER TABLE users
      ADD CONSTRAINT uq_users_login_id UNIQUE (login_id);
  END IF;

  -- 3) 기존에 user_id 컬럼이 있다면 제거(있을 때만)
  IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_name = 'refresh_tokens'
        AND column_name = 'user_id'
  ) THEN
    ALTER TABLE refresh_tokens
      DROP COLUMN user_id;
  END IF;

  -- 4) FK 추가(있을 때만 건너뜀)
  IF NOT EXISTS (
      SELECT 1 FROM information_schema.table_constraints
      WHERE table_name = 'refresh_tokens'
        AND constraint_type = 'FOREIGN KEY'
        AND constraint_name = 'fk_refresh_tokens_login_id'
  ) THEN
    ALTER TABLE refresh_tokens
      ADD CONSTRAINT fk_refresh_tokens_login_id
        FOREIGN KEY (login_id) REFERENCES users(login_id) ON DELETE CASCADE;
  END IF;

  -- 5) 인덱스
  IF NOT EXISTS (
      SELECT 1 FROM pg_class c
      JOIN pg_namespace n ON n.oid = c.relnamespace
      WHERE c.relkind = 'i'
        AND c.relname = 'idx_refresh_tokens_login_id'
        AND n.nspname = 'public'
  ) THEN
    CREATE INDEX idx_refresh_tokens_login_id ON refresh_tokens(login_id);
  END IF;

  -- 필요시 NOT NULL로 바꾸려면(테이블이 비어있을 때만 안전)
  -- ALTER TABLE refresh_tokens ALTER COLUMN login_id SET NOT NULL;
END
$$;
