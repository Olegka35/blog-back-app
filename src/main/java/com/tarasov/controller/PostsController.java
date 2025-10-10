package com.tarasov.controller;


import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.model.dto.posts.PostResponse;
import com.tarasov.model.dto.posts.PostUpdateRequest;
import com.tarasov.service.PostsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/posts")
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping
    public List<PostResponse> getPosts(@RequestParam("search") String search,
                                       @RequestParam("pageSize") int pageSize) {
        List<PostResponse> posts = new ArrayList<>();
        posts.add(new PostResponse(1, "Test Post", "Text", List.of("tag1", "tag2"), 0, 1));
        posts.add(new PostResponse(2, "Test Post 2", "Text2 21", List.of(), 0, 0));
        return posts;
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
