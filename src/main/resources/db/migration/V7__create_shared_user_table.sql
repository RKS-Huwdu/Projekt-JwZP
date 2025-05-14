CREATE TABLE shared_user (
    place_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (place_id, user_id),
    CONSTRAINT fk_place_new FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_new2 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);