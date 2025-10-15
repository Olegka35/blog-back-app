package com.tarasov.repository;

import com.tarasov.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository {

    List<Comment> getComments(long postId);
    Optional<Comment> getComment(long postId, long commentId);
    Comment addComment(long postId, String text);
    void updateComment(long postId, long commentId, String text);
    void deleteComment(long postId, long commentId);
    void deletePostComments(long postId);
}
