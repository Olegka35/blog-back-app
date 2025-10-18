package com.tarasov.blog.controller;


import com.tarasov.blog.model.dto.comments.CommentAddRequest;
import com.tarasov.blog.model.dto.comments.CommentResponse;
import com.tarasov.blog.model.dto.comments.CommentUpdateRequest;
import com.tarasov.blog.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost")
public class CommentsController {

    private final CommentsService commentsService;

    @GetMapping
    public List<CommentResponse> getPostComments(@PathVariable("postId") long postId) {
        return commentsService.getComments(postId);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable("postId") long postId,
                                      @PathVariable("commentId") long commentId) {
        return commentsService.getComment(postId, commentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CommentResponse addCommentToPost(@PathVariable("postId") long postId,
                                            @RequestBody CommentAddRequest request) {
        if (postId != request.postId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post ID mismatch");
        }
        return commentsService.addComment(request);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updatePostComment(@PathVariable("postId") long postId,
                                             @PathVariable("commentId") long commentId,
                                             @RequestBody CommentUpdateRequest request) {
        if (postId != request.postId() || commentId != request.id()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID mismatch");
        }
        return commentsService.updateComment(request);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deletePostComment(@PathVariable("postId") long postId,
                                             @PathVariable("commentId") long commentId) {
        commentsService.deleteComment(postId, commentId);
        return ResponseEntity.ok().build();
    }
}
