CREATE TABLE advertisements (
    id SERIAL PRIMARY KEY,
    description VARCHAR(60) NOT NULL,
    item_id INT NOT NULL,
    advertiser_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(11) NOT NULL,
    is_active BOOLEAN NOT NULL,
    item_video_url VARCHAR(60),
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(advertiser_id) REFERENCES users(id)
);

CREATE TABLE advertisement_images (
    display_order INT,
    advertisement_id INT,
    image_url VARCHAR(60) NOT NULL UNIQUE,
    PRIMARY KEY(display_order, advertisement_id),
    FOREIGN KEY(advertisement_id) REFERENCES advertisements(id)
);