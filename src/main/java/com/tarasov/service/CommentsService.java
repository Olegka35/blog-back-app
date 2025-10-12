package com.tarasov.service;

import com.tarasov.model.dto.comments.CommentAddRequest;
import com.tarasov.model.dto.comments.CommentResponse;
import com.tarasov.model.dto.comments.CommentUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CommentsService {
    List<CommentResponse> getComments(long postId);
    Optional<CommentResponse> getComment(long postId, long commentId);
    CommentResponse addComment(CommentAddRequest request);
    CommentResponse updateComment(CommentUpdateRequest request);
    void deleteComment(long postId, long commentId);
}
