package com.tarasov.blog.repository;


import com.tarasov.blog.model.Post;
import com.tarasov.blog.model.PostSearchCondition;
import com.tarasov.blog.model.dto.posts.PostSearchResult;

import java.util.Optional;

public interface PostsRepository {
    Optional<Post> findPost(long id);
    Post createPost(String title, String text);
    void updatePost(long id, String title, String text);
    void createTag(long postId, String tag);
    void deleteTag(long postId, String tag);
    PostSearchResult searchPosts(PostSearchCondition condition);
    void deletePost(long id);
    void deletePostTags(long postId);
    int incrementLikeCount(long postId);
    void updatePostImage(long postId, byte[] image);
    byte[] getPostImage(long postId);
}

