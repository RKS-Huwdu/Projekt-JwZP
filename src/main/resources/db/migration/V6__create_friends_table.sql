CREATE TABLE friends (
                         id SERIAL PRIMARY KEY,
                         created_at TIMESTAMP,
                         receiver_id BIGINT,
                         requester_id BIGINT,
                         status VARCHAR(50)
);
