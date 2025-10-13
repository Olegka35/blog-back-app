package com.tarasov.service;

import com.tarasov.model.Post;
import com.tarasov.model.PostSearchCondition;
import com.tarasov.model.dto.posts.*;
import com.tarasov.repository.CommentsRepository;
import com.tarasov.repository.PostsRepository;
import com.tarasov.service.impl.PostsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(PostsServiceTest.PostsTestConfiguration.class)
public class PostsServiceTest {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PostsService postsService;

    @BeforeEach
    void resetMocks() {
        reset(postsRepository, commentsRepository);
    }

    @Test
    void searchPosts_successful() {
        String search = "Success #Oleg";
        int pageSize = 5, pageNumber = 1;

        List<Post> posts = List.of(
                new Post(1L, "Success 1", "Text Text Text", List.of("Oleg", "Tag5"), 5, 2),
                new Post(6L, "Success 2", "Text description", List.of("Oleg"), 5, 4),
                new Post(4L, "Success 4", "Text Text Text", List.of("Oleg", "Tag6"), 4, 6),
                new Post(20L, "Success 5", "Text Text Text", List.of("Oleg"), 1, 2),
                new Post(10L, "Success 7", "Text Text Text", List.of("Oleg", "Tag5"), 2, 1)
        );

        ArgumentCaptor<PostSearchCondition> captor = ArgumentCaptor.forClass(PostSearchCondition.class);

        when(postsRepository.searchPosts(any(PostSearchCondition.class)))
                .thenReturn(new PostSearchResult(posts, 12));

        PostListResponse result = postsService.searchPosts(search, pageSize, pageNumber);

        assertEquals(5, result.posts().size());
        assertFalse(result.hasPrev());
        assertTrue(result.hasNext());
        assertEquals(3, result.lastPage());
        verify(postsRepository).searchPosts(captor.capture());
        PostSearchCondition postSearchCondition = captor.getValue();
        assertEquals(1, postSearchCondition.pageNumber());
        assertEquals(5, postSearchCondition.pageSize());
        assertEquals("Success", postSearchCondition.title());
        assertEquals(List.of("Oleg"), postSearchCondition.tags());
    }

    @Test
    void searchPosts_shouldThrow_whenPageNumberTooLarge() {
        when(postsRepository.searchPosts(any())).thenReturn(new PostSearchResult(List.of(), 5));

        assertThrows(IllegalArgumentException.class,
                () -> postsService.searchPosts("test", 5, 2));
    }

    @Test
    void getPostById_found() {
        Post post = new Post(1L, "Title", "Text", List.of("TestTag"), 0, 0);
        when(postsRepository.findPost(1L)).thenReturn(Optional.of(post));

        Optional<PostResponse> result = postsService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals("Title", result.get().title());
        verify(postsRepository).findPost(1L);
    }

    @Test
    void getPostById_notFound() {
        when(postsRepository.findPost(1L)).thenReturn(Optional.empty());
        Optional<PostResponse> result = postsService.getPostById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void createPost_success() {
        PostCreateRequest req = new PostCreateRequest("Title", "Text", List.of("tag1", "tag2"));
        Post post = new Post(1L, "Title", "Text", new ArrayList<>(), 0, 0);

        when(postsRepository.createPost("Title", "Text")).thenReturn(post);

        PostResponse response = postsService.createPost(req);

        assertEquals("Title", response.title());
        verify(postsRepository).createPost("Title", "Text");
        verify(postsRepository, times(2)).createTag(eq(1L), anyString());
    }

    @Test
    void updatePost_success() {
        long id = 1L;
        PostUpdateRequest req = new PostUpdateRequest(id, "Updated", "New Text", List.of("tag1", "tag2"));
        Post existing = new Post(id, "Old", "Old text", List.of("tag1"), 0, 0);

        when(postsRepository.findPost(id)).thenReturn(Optional.of(existing));

        PostResponse response = postsService.updatePost(id, req);

        assertEquals("Updated", response.title());
        verify(postsRepository).updatePost(id, "Updated", "New Text");
        verify(postsRepository).createTag(id, "tag2");
    }

    @Test
    void updatePost_idMismatch() {
        PostUpdateRequest req = new PostUpdateRequest(2L, "T", "X", List.of());
        assertThrows(ResponseStatusException.class, () -> postsService.updatePost(1L, req));
    }

    @Test
    void updatePost_notFound() {
        when(postsRepository.findPost(1L)).thenReturn(Optional.empty());
        PostUpdateRequest req = new PostUpdateRequest(1L, "T", "X", List.of());
        assertThrows(ResponseStatusException.class, () -> postsService.updatePost(1L, req));
    }

    @Test
    void deletePost_success() {
        postsService.deletePost(10L);
        verify(postsRepository).deletePostTags(10L);
        verify(commentsRepository).deletePostComments(10L);
        verify(postsRepository).deletePost(10L);
    }

    @Test
    void incrementLikeCount_success() {
        when(postsRepository.incrementLikeCount(1L)).thenReturn(5);
        int result = postsService.incrementLikeCount(1L);
        assertEquals(5, result);
        verify(postsRepository).incrementLikeCount(1L);
    }

    @Test
    void addImageToPost_success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("testdata".getBytes());

        postsService.addImageToPost(1L, file);

        verify(postsRepository).updatePostImage(1L, "testdata".getBytes());
    }

    @Test
    void getPostImage_success() {
        byte[] image = "bytes".getBytes();
        when(postsRepository.getPostImage(1L)).thenReturn(image);

        byte[] result = postsService.getPostImage(1L);

        assertArrayEquals(image, result);
        verify(postsRepository).getPostImage(1L);
    }

    @Configuration
    public static class PostsTestConfiguration {

        @Bean
        public PostsService postsService(PostsRepository postsRepository,
                                         CommentsRepository commentsRepository) {
            return new PostsServiceImpl(postsRepository, commentsRepository, 128);
        }

        @Bean
        public PostsRepository postsRepository() {
            return mock(PostsRepository.class);
        }

        @Bean
        public CommentsRepository commentsRepository() {
            return mock(CommentsRepository.class);
        }
    }
}
