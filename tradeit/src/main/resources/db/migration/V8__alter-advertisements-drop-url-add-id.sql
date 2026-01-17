ALTER TABLE advertisements
    DROP COLUMN item_video_url,
    ADD COLUMN video_slug UUID NOT NULL UNIQUE;

ALTER TABLE advertisement_images
    DROP COLUMN image_url,
    ADD COLUMN image_slug UUID NOT NULL UNIQUE;
