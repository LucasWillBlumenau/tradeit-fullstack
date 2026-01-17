ALTER TABLE advertisements
    ADD COLUMN trading_item_id INT NOT NULL,
    ADD COLUMN extra_money_amount_required DECIMAL(10, 2) NOT NULL,
    ADD FOREIGN KEY(trading_item_id) REFERENCES items(id);
