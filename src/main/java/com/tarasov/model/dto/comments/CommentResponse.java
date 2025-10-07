package com.tarasov.model.dto.comments;

public record CommentResponse(
        long id,
        String text,
        long postId
) {
}
