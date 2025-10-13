CREATE TABLE IF NOT EXISTS posts (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    text VARCHAR(255) NOT NULL,
    likes_count INT NOT NULL DEFAULT 0,
    image BYTEA
    );

CREATE TABLE IF NOT EXISTS tags (
                                    post_id BIGINT NOT NULL,
                                    tag VARCHAR(255) NOT NULL,
    CONSTRAINT posts_tags_post_id_fkey
    FOREIGN KEY (post_id)
    REFERENCES posts (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGSERIAL PRIMARY KEY,
                                        post_id BIGINT NOT NULL,
                                        text VARCHAR(255) NOT NULL,
    CONSTRAINT comments_post_id_fkey
    FOREIGN KEY (post_id)
    REFERENCES posts (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );