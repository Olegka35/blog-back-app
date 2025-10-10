package com.tarasov.repository.impl;

import com.tarasov.model.Post;
import com.tarasov.repository.PostsRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class JdbcPostsRepositoryImpl implements PostsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String FIND_POST_QUERY = """
        SELECT
            id,
            title,
            text,
            likes_count,
            (SELECT COUNT(1) FROM comments c WHERE c.post_id = p.id) comments_count
        FROM posts p
        WHERE id = :id
        """;
    private final String FIND_TAGS_BY_POST_ID = "SELECT tag FROM tags WHERE post_id = :id";
    private final String CREATE_POST_QUERY = "INSERT INTO posts (title, text) VALUES (:title, :text) RETURNING id";
    private final String CREATE_TAG_QUERY = "INSERT INTO tags (post_id, tag) VALUES (:post_id, :tag)";

    public JdbcPostsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Post> findPost(long id) {
        return jdbcTemplate.query(
                FIND_POST_QUERY,
                Map.of("id", id),
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        getTagsByPostId(rs.getLong("id")),
                        rs.getInt("likes_count"),
                        rs.getInt("comments_count")
                )
        ).stream().findFirst();
    }

    @Override
    public Post createPost(String title, String text) {
        long id = jdbcTemplate.queryForObject(CREATE_POST_QUERY,
                Map.of("title", title, "text", text),
                Long.class);
        return new Post(id, title, text, new ArrayList<>(), 0, 0);
    }

    @Override
    public void createTag(long postId, String tag) {
        jdbcTemplate.update(CREATE_TAG_QUERY, Map.of("post_id", postId, "tag", tag));
    }

    private List<String> getTagsByPostId(long postId) {
        return jdbcTemplate.queryForList(FIND_TAGS_BY_POST_ID, Map.of("id", postId), String.class);
    }
}
