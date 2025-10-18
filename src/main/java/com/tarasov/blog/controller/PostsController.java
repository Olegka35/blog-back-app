package com.tarasov.blog.controller;


import com.tarasov.blog.model.dto.posts.PostCreateRequest;
import com.tarasov.blog.model.dto.posts.PostListResponse;
import com.tarasov.blog.model.dto.posts.PostResponse;
import com.tarasov.blog.model.dto.posts.PostUpdateRequest;
import com.tarasov.blog.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost")
public class PostsController {

    private final PostsService postsService;

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
        return postsService.updatePost(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") long id) {
        postsService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public int addLike(@PathVariable("id") long postId) {
        return postsService.incrementLikeCount(postId);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addImage(@PathVariable("id") long id,
                                         @RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        postsService.addImageToPost(id, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/image")
    public byte[] getImage(@PathVariable("id") long id) {
        return postsService.getPostImage(id);
    }
}
