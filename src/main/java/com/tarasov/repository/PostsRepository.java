package com.tarasov.repository;


import com.tarasov.model.Post;

import java.util.Optional;

public interface PostsRepository {
    Optional<Post> findPost(long id);
    Post createPost(String title, String text);
    void createTag(long postId, String tag);
}

