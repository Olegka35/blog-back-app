package com.tarasov.blog.repository.sql;

public class SQLConstants {

    public static class Posts
    {
        public static final String FIND_TAGS_BY_POST_ID = "SELECT tag FROM tags WHERE post_id = :id";
        public static final String UPDATE_POST_QUERY = "UPDATE posts SET title = :title, text = :text WHERE id = :id";
        public static final String CREATE_TAG_QUERY = "INSERT INTO tags (post_id, tag) VALUES (:post_id, :tag)";
        public static final String DELETE_TAG_QUERY = "DELETE FROM tags WHERE post_id = :post_id AND tag = :tag";
        public static final String COUNT_POSTS_QUERY = """
            SELECT COUNT(1)
            FROM posts p
            """;
        public static final String SEARCH_POSTS_QUERY = """
            SELECT
                id,
                title,
                text,
                likes_count,
                (SELECT COUNT(1) FROM comments c WHERE c.post_id = p.id) comments_count
            FROM posts p
            """;
        public static final String SEARCH_POST_BY_TITLE_SUBQUERY = "p.title LIKE :title";
        public static final String SEARCH_POST_BY_TAG_SUBQUERY = " EXISTS (SELECT tag FROM tags WHERE post_id = p.id AND tag = :%s)";
        public static final String DELETE_POST_QUERY = "DELETE FROM posts WHERE id = :id";
        public static final String DELETE_TAGS_BY_POST_ID_QUERY = "DELETE FROM tags WHERE post_id = :post_id";
        public static final String INCREMENT_LIKE_COUNT_QUERY = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = :id RETURNING likes_count";
        public static final String UPDATE_POST_IMAGE_QUERY = "UPDATE posts SET image = :image WHERE id = :id";
        public static final String GET_POST_IMAGE_QUERY = "SELECT image FROM posts WHERE id = :id";
    }


    public static class Comments
    {
        public static final String GET_COMMENTS_QUERY = "SELECT id, post_id, text FROM comments WHERE post_id = :post_id";
        public static final String GET_COMMENT_QUERY = "SELECT id, post_id, text FROM comments WHERE id = :comment_id AND post_id = :post_id";
        public static final String UPDATE_COMMENT_QUERY = "UPDATE comments SET text = :text WHERE id = :comment_id AND post_id = :post_id";
        public static final String DELETE_COMMENT_QUERY = "DELETE FROM comments WHERE id = :comment_id AND post_id = :post_id";
        public static final String DELETE_COMMENTS_BY_POST_ID_QUERY = "DELETE FROM comments WHERE post_id = :post_id";
    }

}
