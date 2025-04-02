CREATE TABLE place_user (
    place_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (place_id, user_id),
    CONSTRAINT fk_place FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_new FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);