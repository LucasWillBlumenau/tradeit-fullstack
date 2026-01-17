ALTER TABLE users
    DROP COLUMN username,
    DROP COLUMN phone_number,
    ADD COLUMN password VARCHAR(255) NOT NULL;