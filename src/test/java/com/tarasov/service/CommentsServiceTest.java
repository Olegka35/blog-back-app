package com.tarasov.service;

import com.tarasov.model.Comment;
import com.tarasov.model.dto.comments.CommentAddRequest;
import com.tarasov.model.dto.comments.CommentResponse;
import com.tarasov.model.dto.comments.CommentUpdateRequest;
import com.tarasov.repository.CommentsRepository;
import com.tarasov.service.impl.CommentsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(CommentsServiceTest.CommentsTestConfiguration.class)
public class CommentsServiceTest {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private CommentsService commentsService;

    @Test
    void getComments_returnsSuccessfully() {
        long postId = 100L;
        Comment comment1 = new Comment(1L, postId, "Text 1");
        Comment comment2 = new Comment(2L, postId, "Text 2");
        when(commentsRepository.getComments(postId)).thenReturn(List.of(comment1, comment2));

        List<CommentResponse> result = commentsService.getComments(postId);

        assertEquals(2, result.size());
        assertEquals("Text 2", result.get(1).text());
        verify(commentsRepository).getComments(postId);
    }

    @Test
    void getComment_whenExists() {
        long postId = 1L;
        long commentId = 10L;
        Comment comment = new Comment(commentId, postId, "Comment text");
        when(commentsRepository.getComment(postId, commentId)).thenReturn(Optional.of(comment));

        Optional<CommentResponse> result = commentsService.getComment(postId, commentId);

        assertTrue(result.isPresent());
        assertEquals(commentId, result.get().id());
        assertEquals("Comment text", result.get().text());
        verify(commentsRepository).getComment(postId, commentId);
    }

    @Test
    void getComment_whenNotFound() {
        long postId = 1L;
        long commentId = 42L;
        when(commentsRepository.getComment(postId, commentId)).thenReturn(Optional.empty());

        Optional<CommentResponse> result = commentsService.getComment(postId, commentId);

        assertTrue(result.isEmpty());
        verify(commentsRepository).getComment(postId, commentId);
    }

    @Test
    void addComment_success() {
        CommentAddRequest request = new CommentAddRequest("Hello", 5L);
        Comment newComment = new Comment(1000L, 5L, "Hello");
        when(commentsRepository.addComment(request.postId(), request.text())).thenReturn(newComment);

        CommentResponse result = commentsService.addComment(request);

        assertEquals(1000L, result.id());
        assertEquals("Hello", result.text());
        verify(commentsRepository).addComment(5L, "Hello");
    }

    @Test
    void updateComment_success() {
        CommentUpdateRequest request = new CommentUpdateRequest(1000L, "Updated text", 10L);

        commentsService.updateComment(request);

        verify(commentsRepository).updateComment(10L, 1000L, "Updated text");
        CommentResponse result = commentsService.updateComment(request);
        assertEquals("Updated text", result.text());
        assertEquals(10L, result.postId());
    }

    @Test
    void deleteComment_success() {
        long postId = 1L;
        long commentId = 3L;

        commentsService.deleteComment(postId, commentId);

        verify(commentsRepository).deleteComment(postId, commentId);
    }


    @Configuration
    public static class CommentsTestConfiguration {

        @Bean
        public CommentsService commentsService(CommentsRepository commentsRepository) {
            return new CommentsServiceImpl(commentsRepository);
        }

        @Bean
        public CommentsRepository commentsRepository() {
            return mock(CommentsRepository.class);
        }
    }
}
