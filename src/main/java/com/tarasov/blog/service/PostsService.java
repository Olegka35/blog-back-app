package com.tarasov.blog.service;

import com.tarasov.blog.model.dto.posts.PostCreateRequest;
import com.tarasov.blog.model.dto.posts.PostListResponse;
import com.tarasov.blog.model.dto.posts.PostResponse;
import com.tarasov.blog.model.dto.posts.PostUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PostsService {
    PostListResponse searchPosts(String search, int pageSize, int pageNumber);
    Optional<PostResponse> getPostById(long id);
    PostResponse createPost(PostCreateRequest request);
    PostResponse updatePost(long id, PostUpdateRequest request);
    void deletePost(long id);
    int incrementLikeCount(long postId);
    void addImageToPost(long postId, MultipartFile image);
    byte[] getPostImage(long postId);
}
