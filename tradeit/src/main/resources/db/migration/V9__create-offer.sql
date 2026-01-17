CREATE TABLE offers(
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    advertisement_id INT NOT NULL,
    message VARCHAR(255) NOT NULL,
    additional_money_offer DECIMAL(10, 2),
    video_slug UUID NOT NULL,
    status VARCHAR(8) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(advertisement_id) REFERENCES advertisements(id)
);

CREATE TABLE offer_images (
    id SERIAL PRIMARY KEY,
    offer_id INT NOT NULL,
    image_slug UUID NOT NULL,
    FOREIGN KEY(offer_id) REFERENCES offers(id)
);