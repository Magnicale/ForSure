-- Run once on a database created from the earlier schema.sql.
ALTER TABLE capsules ADD COLUMN IF NOT EXISTS request_key UUID;

UPDATE capsules
SET request_key = gen_random_uuid()
WHERE request_key IS NULL;

ALTER TABLE capsules ALTER COLUMN request_key SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_capsules_owner_request_key
    ON capsules(owner_id, request_key);
