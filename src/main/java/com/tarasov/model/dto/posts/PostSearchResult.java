package com.tarasov.model.dto.posts;

import com.tarasov.model.Post;

import java.util.List;

public record PostSearchResult(
        List<Post> posts,
        int totalCount
) {
}
