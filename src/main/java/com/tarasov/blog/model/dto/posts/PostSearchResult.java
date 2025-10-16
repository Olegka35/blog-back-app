package com.tarasov.blog.model.dto.posts;

import com.tarasov.blog.model.Post;

import java.util.List;

public record PostSearchResult(
        List<Post> posts,
        int totalCount
) {
}
