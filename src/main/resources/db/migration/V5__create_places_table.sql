CREATE TABLE places (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    address VARCHAR,
    country VARCHAR,
    city VARCHAR,
    note VARCHAR,
    is_public BOOLEAN,
    post_date TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_place_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_place_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);