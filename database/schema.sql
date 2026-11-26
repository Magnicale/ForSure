CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(80) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS media_files (
    id BIGSERIAL PRIMARY KEY,
    storage_key VARCHAR(80) NOT NULL UNIQUE,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(80) NOT NULL,
    size_bytes BIGINT NOT NULL CHECK (size_bytes > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS capsules (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id),
    media_file_id BIGINT NOT NULL UNIQUE REFERENCES media_files(id),
    request_key UUID NOT NULL,
    title VARCHAR(120) NOT NULL,
    note VARCHAR(4000) NOT NULL,
    unlock_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS capsule_tags (
    capsule_id BIGINT NOT NULL REFERENCES capsules(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (capsule_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_capsules_unlock_at ON capsules(unlock_at);
CREATE INDEX IF NOT EXISTS idx_capsules_owner_id ON capsules(owner_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_capsules_owner_request_key ON capsules(owner_id, request_key);

INSERT INTO categories(name) VALUES ('Воспоминания'), ('Цели'), ('Путешествия')
ON CONFLICT (name) DO NOTHING;

INSERT INTO tags(name) VALUES ('Семья'), ('Друзья'), ('Идеи'), ('Планы')
ON CONFLICT (name) DO NOTHING;
