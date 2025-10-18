package com.tarasov.blog.repository.impl;


import com.tarasov.blog.model.Comment;
import com.tarasov.blog.repository.CommentsRepository;
import com.tarasov.blog.repository.sql.SQLConstants;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcCommentsRepository implements CommentsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCommentsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getComments(long postId) {
        return jdbcTemplate.query(SQLConstants.Comments.GET_COMMENTS_QUERY,
                Map.of("post_id", postId),
                new CommentMapper());
    }

    @Override
    public Optional<Comment> getComment(long postId, long commentId) {
        return jdbcTemplate.query(SQLConstants.Comments.GET_COMMENT_QUERY,
                        Map.of("post_id", postId, "comment_id", commentId),
                        new CommentMapper())
                .stream().findFirst();
    }

    @Override
    public Comment addComment(long postId, String text) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("comments")
                .usingColumns("post_id", "text")
                .usingGeneratedKeyColumns("id");
        Number id = insert.executeAndReturnKey(Map.of("post_id", postId, "text", text));
        return new Comment(id.longValue(), postId, text);
    }

    @Override
    public void updateComment(long postId, long commentId, String text) {
        int rowsAffected = jdbcTemplate.update(SQLConstants.Comments.UPDATE_COMMENT_QUERY,
                Map.of("post_id", postId,
                        "comment_id", commentId,
                        "text", text));
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        int rows = jdbcTemplate.update(SQLConstants.Comments.DELETE_COMMENT_QUERY,
                Map.of("post_id", postId, "comment_id", commentId));
        if (rows == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
    }

    @Override
    public void deletePostComments(long postId) {
        jdbcTemplate.update(SQLConstants.Comments.DELETE_COMMENTS_BY_POST_ID_QUERY,
                Map.of("post_id", postId));
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
