package com.tarasov.model.dto.comments;

public record CommentAddRequest(
        String text,
        long postId
) {
}
