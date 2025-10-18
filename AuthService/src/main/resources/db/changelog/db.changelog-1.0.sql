--liquibase formatted sql

--changeset voodzz:1
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL PRIMARY KEY,
    role VARCHAR(128) NOT NULL UNIQUE
);

--changeset voodzz:2
CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    phone    VARCHAR(128)                 NOT NULL UNIQUE,
    password VARCHAR(255)                 NOT NULL,
    role_id  BIGINT REFERENCES roles (id) NOT NULL
);

--changeset voodzz:3
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(256) NOT NULL UNIQUE,
    user_id     BIGINT REFERENCES users (id) ON DELETE CASCADE NOT NULL,
    expiry_date TIMESTAMP    NOT NULL
);

--changeset voodzz:4
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_users_phone ON users (phone);

--changeset voodzz:5
INSERT INTO roles (role)
VALUES ('ROLE_PATIENT'),
       ('ROLE_DOCTOR'),
       ('ROLE_OPERATOR'),
       ('ROLE_MANAGER')
ON CONFLICT (role) DO NOTHING;