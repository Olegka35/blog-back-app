package com.tarasov.model.dto.posts;

import java.util.List;

public record PostCreateRequest(
        String title,
        String text,
        List<String> tags
) {
}
