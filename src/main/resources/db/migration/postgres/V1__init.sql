-- ------------------------------------------------------------
-- Flyway Migration: V1__init_users_and_refresh_tokens.sql
-- Target: MySQL 8.0+, InnoDB, utf8mb4
-- ------------------------------------------------------------

SET NAMES utf8mb4;
SET SESSION sql_mode='STRICT_ALL_TABLES';

-- ===========================
-- 1) users
-- ===========================
CREATE TABLE IF NOT EXISTS users (
  uid             BIGINT NOT NULL AUTO_INCREMENT,
  username        VARCHAR(20)  NOT NULL,
  login_id        VARCHAR(30)  NULL,
  password        VARCHAR(60)  NULL,
  email           VARCHAR(30)  NULL,
  role            VARCHAR(20)  NOT NULL,
  provider        VARCHAR(255) NULL,
  provider_id     VARCHAR(255) NULL,
  failed_attempts INT          NOT NULL DEFAULT 0,
  last_failed_at  DATETIME(6)  NULL,
  locked_until    DATETIME(6)  NULL,
  PRIMARY KEY (uid),
  KEY idx_login_id (login_id),
  KEY idx_email (email),
  KEY idx_provider_providerId (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ===========================
-- 2) refresh_tokens
-- ===========================
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id           BIGINT NOT NULL AUTO_INCREMENT,
  login_id     VARCHAR(255) NULL,
  token_hash   VARCHAR(128) NOT NULL,
  fingerprint  VARCHAR(255) NULL,
  expires_at   DATETIME(6)  NOT NULL,
  persistent   BOOLEAN NULL,
  revoked      BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_tokenHash (token_hash),
  KEY idx_expiresAt (expires_at),
  KEY idx_loginId_fingerprint (login_id, fingerprint)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
