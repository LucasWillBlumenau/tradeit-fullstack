ALTER TABLE advertisement_images ADD COLUMN content_type VARCHAR(10) NOT NULL;
ALTER TABLE advertisements ADD COLUMN video_content_type VARCHAR(10) NOT NULL;
ALTER TABLE offer_images ADD COLUMN content_type VARCHAR(10) NOT NULL;
ALTER TABLE offers ADD COLUMN video_content_type VARCHAR(10) NOT NULL;
