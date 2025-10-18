package com.tarasov.blog.repository.impl;

import com.tarasov.blog.model.Post;
import com.tarasov.blog.model.PostSearchCondition;
import com.tarasov.blog.model.dto.posts.PostSearchResult;
import com.tarasov.blog.repository.PostsRepository;
import com.tarasov.blog.repository.sql.SQLConstants;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Repository
public class JdbcPostsRepositoryImpl implements PostsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPostsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Post> findPost(long id) {
        return jdbcTemplate.query(
                SQLConstants.Posts.SEARCH_POSTS_QUERY + " WHERE id = :id",
                Map.of("id", id),
                        new PostMapper()
        ).stream()
                .peek(post -> post.setTags(getTagsByPostId(post.getId())))
                .findFirst();
    }

    @Override
    public Post createPost(String title, String text) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("posts")
                .usingColumns("title", "text")
                .usingGeneratedKeyColumns("id");
        Number id = insert.executeAndReturnKey(Map.of("title", title, "text", text));
        return new Post(id.longValue(), title, text, new ArrayList<>(), 0, 0);
    }

    @Override
    public void updatePost(long id, String title, String text) {
        jdbcTemplate.update(SQLConstants.Posts.UPDATE_POST_QUERY,
                Map.of("title", title, "text", text, "id", id));
    }

    @Override
    public void createTag(long postId, String tag) {
        jdbcTemplate.update(SQLConstants.Posts.CREATE_TAG_QUERY,
                Map.of("post_id", postId, "tag", tag));
    }

    @Override
    public void deleteTag(long postId, String tag) {
        jdbcTemplate.update(SQLConstants.Posts.DELETE_TAG_QUERY,
                Map.of("post_id", postId, "tag", tag));
    }

    @Override
    public PostSearchResult searchPosts(PostSearchCondition condition) {
        StringBuilder searchQuery = new StringBuilder(SQLConstants.Posts.SEARCH_POSTS_QUERY);
        StringBuilder countQuery = new StringBuilder(SQLConstants.Posts.COUNT_POSTS_QUERY);
        Map<String, Object> params = new HashMap<>();
        List<String> conditions = new ArrayList<>();
        translateSearchConditions(condition, conditions, params);
        if (!CollectionUtils.isEmpty(conditions)) {
            searchQuery.append(" WHERE ").append(String.join(" AND ", conditions));
            countQuery.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        searchQuery.append(" LIMIT :limit OFFSET :offset");
        params.put("limit", condition.pageSize());
        params.put("offset", (condition.pageNumber() - 1) * condition.pageSize());
        Integer count = jdbcTemplate.queryForObject(countQuery.toString(), params, Integer.class);
        if (count == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during post count");
        }
        List<Post> posts = jdbcTemplate.query(searchQuery.toString(), params, new PostMapper())
                .stream()
                .peek(post -> post.setTags(getTagsByPostId(post.getId())))
                .toList();
        return new PostSearchResult(posts, count);
    }

    @Override
    public void deletePost(long id) {
        int rowsDeleted = jdbcTemplate.update(SQLConstants.Posts.DELETE_POST_QUERY,
                Map.of("id", id));
        if (rowsDeleted == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    @Override
    public void deletePostTags(long postId) {
        jdbcTemplate.update(SQLConstants.Posts.DELETE_TAGS_BY_POST_ID_QUERY,
                Map.of("post_id", postId));
    }

    @Override
    public int incrementLikeCount(long postId) {
        Integer newLikeCount;
        try {
            newLikeCount = jdbcTemplate.queryForObject(SQLConstants.Posts.INCREMENT_LIKE_COUNT_QUERY,
                    Map.of("id", postId), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return Optional.ofNullable(newLikeCount).orElse(0);
    }

    @Override
    public void updatePostImage(long postId, byte[] image) {
        int rowsAffected = jdbcTemplate.update(SQLConstants.Posts.UPDATE_POST_IMAGE_QUERY,
                Map.of("id", postId, "image", image));
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
    }

    @Override
    public byte[] getPostImage(long postId) {
        try {
            return jdbcTemplate.queryForObject(SQLConstants.Posts.GET_POST_IMAGE_QUERY,
                    Map.of("id", postId), byte[].class);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested image not found");
        }
    }

    private List<String> getTagsByPostId(long postId) {
        return jdbcTemplate.queryForList(SQLConstants.Posts.FIND_TAGS_BY_POST_ID,
                Map.of("id", postId), String.class);
    }

    private static class PostMapper implements RowMapper<Post> {

        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Post(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("text"),
                    List.of(),
                    rs.getInt("likes_count"),
                    rs.getInt("comments_count")
            );
        }

    }

    void translateSearchConditions(PostSearchCondition condition, List<String> conditions, Map<String, Object> params) {
        if (StringUtils.hasText(condition.title())) {
            conditions.add(SQLConstants.Posts.SEARCH_POST_BY_TITLE_SUBQUERY);
            params.put("title", "%" + condition.title() + "%");
        }
        if (!CollectionUtils.isEmpty(condition.tags())) {
            AtomicInteger counter = new AtomicInteger(1);
            condition.tags().forEach(tag -> {
                String keyName = "tag_" + counter.getAndIncrement();
                conditions.add(SQLConstants.Posts.SEARCH_POST_BY_TAG_SUBQUERY.formatted(keyName));
                params.put(keyName, tag);
            });
        }
    }
}
