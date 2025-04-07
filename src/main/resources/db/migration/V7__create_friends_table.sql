CREATE TABLE friends (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         created_at TIMESTAMP,
                         receiver_id BIGINT,
                         requester_id BIGINT,
                         status VARCHAR(50)
);
