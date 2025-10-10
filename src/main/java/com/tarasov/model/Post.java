package com.tarasov.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Post {
    private long id;
    private String title;
    private String text;
    private List<String> tags;
    private int likesCount;
    private int commentsCount;
}
