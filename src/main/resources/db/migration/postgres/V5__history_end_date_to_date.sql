-- V5__history_end_date_to_date.sql
-- TIMESTAMPTZ -> DATE 로 타입 변경 (UTC 기준으로 잘라서 날짜만 저장)
ALTER TABLE history
  ALTER COLUMN end_date TYPE DATE USING ((end_date AT TIME ZONE 'UTC')::date);

-- V4 보완용: persistent 컬럼 보장
ALTER TABLE refresh_tokens
  ADD COLUMN IF NOT EXISTS persistent BOOLEAN NOT NULL DEFAULT false;
