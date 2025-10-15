package com.tarasov.model.dto.comments;

public record CommentUpdateRequest(
        long id,
        String text,
        long postId
) {
}
