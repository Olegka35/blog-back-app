package com.tarasov.repository.impl;


import com.tarasov.model.Comment;
import com.tarasov.repository.CommentsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcCommentsRepository implements CommentsRepository {

    private final String GET_COMMENTS_QUERY = "SELECT id, post_id, text FROM comments WHERE post_id = :post_id";
    private final String GET_COMMENT_QUERY = "SELECT id, post_id, text FROM comments WHERE id = :comment_id AND post_id = :post_id";
    private final String ADD_COMMENT_QUERY = "INSERT INTO comments (post_id, text) VALUES(:post_id, :text) RETURNING id";
    private final String UPDATE_COMMENT_QUERY = "UPDATE comments SET text = :text WHERE id = :comment_id AND post_id = :post_id";
    private final String DELETE_COMMENT_QUERY = "DELETE FROM comments WHERE id = :comment_id AND post_id = :post_id";
    private final String DELETE_COMMENTS_BY_POST_ID_QUERY = "DELETE FROM comments WHERE post_id = :post_id";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCommentsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getComments(long postId) {
        return jdbcTemplate.query(GET_COMMENTS_QUERY,
                Map.of("post_id", postId),
                new CommentMapper());
    }

    @Override
    public Optional<Comment> getComment(long postId, long commentId) {
        return jdbcTemplate.query(GET_COMMENT_QUERY,
                        Map.of("post_id", postId, "comment_id", commentId),
                        new CommentMapper())
                .stream().findFirst();
    }

    @Override
    public Comment addComment(long postId, String text) {
        Long id = jdbcTemplate.queryForObject(ADD_COMMENT_QUERY,
                Map.of("post_id", postId, "text", text),
                Long.class);
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during comment creation");
        }
        return new Comment(id, postId, text);
    }

    @Override
    public void updateComment(long postId, long commentId, String text) {
        int rowsAffected = jdbcTemplate.update(UPDATE_COMMENT_QUERY,
                Map.of("post_id", postId,
                        "comment_id", commentId,
                        "text", text));
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        int rows = jdbcTemplate.update(DELETE_COMMENT_QUERY, Map.of("post_id", postId, "comment_id", commentId));
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @Override
    public void deletePostComments(long postId) {
        jdbcTemplate.update(DELETE_COMMENTS_BY_POST_ID_QUERY, Map.of("post_id", postId));
    }

    private static class CommentMapper implements RowMapper<Comment> {

        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Comment(
                    rs.getLong("id"),
                    rs.getLong("post_id"),
                    rs.getString("text")
            );
        }

    }

}
