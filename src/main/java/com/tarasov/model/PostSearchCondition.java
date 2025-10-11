package com.tarasov.model;

import java.util.List;

public record PostSearchCondition(
        String title,
        List<String> tags,
        int pageSize,
        int pageNumber
) {
}
