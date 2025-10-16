package com.tarasov.blog.service;

import com.tarasov.blog.model.dto.comments.CommentAddRequest;
import com.tarasov.blog.model.dto.comments.CommentResponse;
import com.tarasov.blog.model.dto.comments.CommentUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CommentsService {
    List<CommentResponse> getComments(long postId);
    Optional<CommentResponse> getComment(long postId, long commentId);
    CommentResponse addComment(CommentAddRequest request);
    CommentResponse updateComment(CommentUpdateRequest request);
    void deleteComment(long postId, long commentId);
}
