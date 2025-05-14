CREATE TABLE shared_with (
    place_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (place_id, user_id),
    CONSTRAINT fk_shared_place FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    CONSTRAINT fk_shared_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);