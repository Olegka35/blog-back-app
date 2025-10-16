package com.tarasov.blog.model.dto.posts;

import com.tarasov.blog.model.Post;

import java.util.List;

public record PostResponse(
        long id,
        String title,
        String text,
        List<String> tags,
        int likesCount,
        int commentsCount) {

    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(),
                post.getTitle(),
                post.getText(),
                post.getTags(),
                post.getLikesCount(),
                post.getCommentsCount());
    }
}
