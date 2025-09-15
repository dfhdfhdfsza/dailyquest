-- history: TIMESTAMPTZ -> DATE (UTC 기준 날짜만 보존)
ALTER TABLE history
  ALTER COLUMN start_date TYPE DATE USING ((start_date AT TIME ZONE 'UTC')::date),
  ALTER COLUMN end_date   TYPE DATE USING ((end_date   AT TIME ZONE 'UTC')::date);

-- refresh_tokens: 누락 가능성 있는 컬럼 보강
ALTER TABLE refresh_tokens
  ADD COLUMN IF NOT EXISTS login_id   VARCHAR(255),
  ADD COLUMN IF NOT EXISTS persistent BOOLEAN NOT NULL DEFAULT FALSE;
