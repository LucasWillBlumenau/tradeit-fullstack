CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY(category_id) REFERENCES categories(id)
);