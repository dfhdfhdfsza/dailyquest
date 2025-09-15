
DO $$
BEGIN
  -- 1) refresh_tokens.login_id 컬럼 없으면 추가
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
     WHERE table_name='refresh_tokens' AND column_name='login_id'
  ) THEN
    ALTER TABLE refresh_tokens ADD COLUMN login_id VARCHAR(191);
  END IF;

  -- 2) users.login_id UNIQUE 보장 (FK 걸 요건)
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes WHERE indexname='ux_users_login_id'
  ) THEN
    CREATE UNIQUE INDEX ux_users_login_id ON users(login_id);
  END IF;

  -- 3) 기존 컬럼에 따라 login_id 백필 (uid 또는 user_id가 있을 때)
  IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name='refresh_tokens' AND column_name='uid') THEN
     UPDATE refresh_tokens rt
        SET login_id = u.login_id
       FROM users u
      WHERE rt.uid = u.uid AND rt.login_id IS NULL;

  ELSIF EXISTS (SELECT 1 FROM information_schema.columns
                  WHERE table_name='refresh_tokens' AND column_name='user_id') THEN
     UPDATE refresh_tokens rt
        SET login_id = u.login_id
       FROM users u
      WHERE rt.user_id = u.uid AND rt.login_id IS NULL;
  END IF;

  -- 4) NULL 없으면 NOT NULL 제약 추가
  IF EXISTS (
      SELECT 1 FROM information_schema.columns
       WHERE table_name='refresh_tokens' AND column_name='login_id' AND is_nullable='YES'
  ) THEN
      IF NOT EXISTS (SELECT 1 FROM refresh_tokens WHERE login_id IS NULL) THEN
         ALTER TABLE refresh_tokens ALTER COLUMN login_id SET NOT NULL;
      END IF;
  END IF;

  -- 5) FK 없으면 추가 (users.login_id ↔ refresh_tokens.login_id)
  IF NOT EXISTS (
     SELECT 1 FROM information_schema.table_constraints
      WHERE table_name='refresh_tokens' AND constraint_name='fk_refresh_tokens_user'
  ) THEN
     ALTER TABLE refresh_tokens
       ADD CONSTRAINT fk_refresh_tokens_user
       FOREIGN KEY (login_id) REFERENCES users(login_id) ON DELETE CASCADE;
  END IF;

  -- (선택) 예전에 잘못 건 FK/인덱스가 있으면 여기서 정리
  -- 예: user_id FK가 남아있다면 DROP CONSTRAINT ... 등
END $$;

-- 보조 인덱스
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_login_fingerprint
  ON refresh_tokens (login_id, fingerprint);
