-- V1__init.sql - initial schema
-- Use BIGSERIAL ids to match JPA entity id types (Long)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  email varchar(255) NOT NULL UNIQUE,
  password_hash varchar(255) NOT NULL,
  full_name varchar(255),
  role varchar(50) NOT NULL DEFAULT 'USER',
  enabled boolean NOT NULL DEFAULT true,
  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now(),
  version BIGINT
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
