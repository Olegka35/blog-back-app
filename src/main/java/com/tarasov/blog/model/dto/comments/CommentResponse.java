package com.tarasov.blog.model.dto.comments;

import com.tarasov.blog.model.Comment;

public record CommentResponse(
        long id,
        String text,
        long postId
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getText(), comment.getPostId());
    }
}
