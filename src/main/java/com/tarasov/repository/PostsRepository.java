package com.tarasov.repository;


import com.tarasov.model.Post;
import com.tarasov.model.PostSearchCondition;
import com.tarasov.model.dto.posts.PostSearchResult;

import java.util.Optional;

public interface PostsRepository {
    Optional<Post> findPost(long id);
    Post createPost(String title, String text);
    void createTag(long postId, String tag);
    PostSearchResult searchPosts(PostSearchCondition condition);
}

