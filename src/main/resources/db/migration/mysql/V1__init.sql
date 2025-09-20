-- 1) users
CREATE TABLE IF NOT EXISTS users (
  uid              BIGINT AUTO_INCREMENT PRIMARY KEY,
  username         VARCHAR(20)  NOT NULL,
  login_id         VARCHAR(30),
  password         VARCHAR(60),
  email            VARCHAR(30),
  role             VARCHAR(20)  NOT NULL,
  provider         VARCHAR(255),
  provider_id      VARCHAR(255),
  failed_attempts  INT          NOT NULL DEFAULT 0,
  last_failed_at   DATETIME(6),
  locked_until     DATETIME(6)
) ENGINE=InnoDB;

CREATE INDEX idx_login_id               ON users (login_id);
CREATE INDEX idx_email                  ON users (email);
CREATE INDEX idx_provider_providerId    ON users (provider, provider_id);

-- 2) refresh_tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  login_id     VARCHAR(255),
  token_hash   VARCHAR(128) NOT NULL,
  fingerprint  VARCHAR(255),
  expires_at   DATETIME(6) NOT NULL,
  persistent   BOOLEAN,
  revoked      BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

CREATE INDEX idx_tokenHash             ON refresh_tokens (token_hash);
CREATE INDEX idx_expiresAt             ON refresh_tokens (expires_at);
CREATE INDEX idx_loginId_fingerprint   ON refresh_tokens (login_id, fingerprint);
