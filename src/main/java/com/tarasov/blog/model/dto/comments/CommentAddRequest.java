package com.tarasov.blog.model.dto.comments;

public record CommentAddRequest(
        String text,
        long postId
) {
}
