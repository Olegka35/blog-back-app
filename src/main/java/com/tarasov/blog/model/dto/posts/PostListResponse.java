package com.tarasov.blog.model.dto.posts;

import java.util.List;

public record PostListResponse(
        List<PostResponse> posts,
        boolean hasPrev,
        boolean hasNext,
        int lastPage
) {
}
