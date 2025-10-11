package com.tarasov.controller;


import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.model.dto.posts.PostListResponse;
import com.tarasov.model.dto.posts.PostResponse;
import com.tarasov.model.dto.posts.PostUpdateRequest;
import com.tarasov.service.PostsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/posts")
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping
    public PostListResponse searchPosts(@RequestParam("search") String search,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("pageNumber") int pageNumber) {
        if (pageSize <= 0 || pageNumber <= 0) {
            throw new IllegalArgumentException("pageSize and pageNumber must be 1 or higher");
        }
        return postsService.searchPosts(search, pageSize, pageNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable("id") long id) {
        return postsService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostResponse createPost(@RequestBody PostCreateRequest request) {
        return postsService.createPost(request);
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable("id") long id,
                                   @RequestBody PostUpdateRequest request) {
        System.out.println(request);
        return new PostResponse(1, "Test Post", "Text", List.of("tag1", "tagd2"), 10, 11);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") long id) {
        return ResponseEntity.ok().build();
    }
}
