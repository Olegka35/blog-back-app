package com.tarasov.blog.model.dto.comments;

public record CommentUpdateRequest(
        long id,
        String text,
        long postId
) {
}
