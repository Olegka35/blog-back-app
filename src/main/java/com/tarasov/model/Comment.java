package com.tarasov.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Comment {
    private long id;
    private long postId;
    private String text;
}
