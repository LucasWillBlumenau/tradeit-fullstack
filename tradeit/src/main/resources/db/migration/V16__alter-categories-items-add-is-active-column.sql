ALTER TABLE items ADD COLUMN is_active BOOLEAN;
ALTER TABLE categories ADD COLUMN is_active BOOLEAN;

UPDATE items SET is_active = TRUE;
UPDATE categories SET is_active = TRUE;

ALTER TABLE items ALTER COLUMN is_active SET NOT NULL;
ALTER TABLE categories ALTER COLUMN is_active SET NOT NULL;