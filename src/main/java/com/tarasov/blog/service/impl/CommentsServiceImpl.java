package com.tarasov.blog.service.impl;


import com.tarasov.blog.model.dto.comments.CommentAddRequest;
import com.tarasov.blog.model.dto.comments.CommentResponse;
import com.tarasov.blog.model.dto.comments.CommentUpdateRequest;
import com.tarasov.blog.repository.CommentsRepository;
import com.tarasov.blog.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentsRepository;

    @Override
    public List<CommentResponse> getComments(long postId) {
        return commentsRepository.getComments(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Override
    public Optional<CommentResponse> getComment(long postId, long commentId) {
        return commentsRepository.getComment(postId, commentId).map(CommentResponse::from);
    }

    @Override
    public CommentResponse addComment(CommentAddRequest request) {
        return CommentResponse.from(commentsRepository.addComment(request.postId(), request.text()));
    }

    @Override
    public CommentResponse updateComment(CommentUpdateRequest request) {
        commentsRepository.updateComment(request.postId(), request.id(), request.text());
        return new CommentResponse(request.id(), request.text(), request.postId());
    }

    @Override
    public void deleteComment(long postId, long commentId) {
        commentsRepository.deleteComment(postId, commentId);
    }
}
