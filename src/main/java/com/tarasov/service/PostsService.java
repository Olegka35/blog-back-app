package com.tarasov.service;

import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.model.dto.posts.PostListResponse;
import com.tarasov.model.dto.posts.PostResponse;

import java.util.Optional;

public interface PostsService {
    PostListResponse searchPosts(String search, int pageSize, int pageNumber);
    Optional<PostResponse> getPostById(long id);
    PostResponse createPost(PostCreateRequest request);
}
