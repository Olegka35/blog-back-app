package com.tarasov.test.repository;


import com.tarasov.blog.model.Post;
import com.tarasov.blog.model.PostSearchCondition;
import com.tarasov.blog.model.dto.posts.PostSearchResult;
import com.tarasov.blog.repository.PostsRepository;
import com.tarasov.test.DataBasePreparator;
import com.tarasov.test.configuration.DataSourceConfiguration;
import com.tarasov.test.configuration.WebConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource("classpath:test-application.properties")
@Disabled
public class PostsRepositoryTest {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private DataBasePreparator dataBasePreparator;

    @BeforeEach
    public void setUp() {
        dataBasePreparator.prepareDatabase();
    }

    @Test
    void getPostTest() {
        Optional<Post> post = postsRepository.findPost(2);
        assertTrue(post.isPresent());
        assertEquals("Spring Boot + H2 пост", post.get().getTitle());
        assertEquals(2, post.get().getTags().size());
    }

    @Test
    void getNonExistingPostTest() {
        Optional<Post> post = postsRepository.findPost(200);
        assertTrue(post.isEmpty());
    }

    @Test
    void searchPostsTest() {
        PostSearchResult result = postsRepository.searchPosts(new PostSearchCondition("пост", List.of("h2"), 10, 1));
        assertEquals(1, result.posts().size());
        assertEquals("Демонстрация интеграции H2 с Spring Boot.", result.posts().get(0).getText());
        assertEquals(1, result.totalCount());
    }

    @Test
    void searchPostsTestIncorrectPage() {
        PostSearchResult result = postsRepository.searchPosts(new PostSearchCondition("пост", List.of("h2"), 10, 2));
        assertEquals(0, result.posts().size());
        assertEquals(1, result.totalCount());
    }

    @Test
    void deletePostTest() {
        postsRepository.deletePost(1);
        Optional<Post> post = postsRepository.findPost(1);
        assertTrue(post.isEmpty());
    }

    @Test
    void updatePostTest() {
        postsRepository.updatePost(4, "Динамическое программирование", "Обзор Project Reactor и WebFlux.");
        Optional<Post> post = postsRepository.findPost(4);
        assertTrue(post.isPresent());
        assertEquals("Динамическое программирование", post.get().getTitle());
    }

    @Test
    void createPostTest() {
        Post createdPost =
                postsRepository.createPost( "Динамическое программирование", "Статья про динамическое программирование");
        assertEquals("Динамическое программирование", createdPost.getTitle());
        assertEquals("Статья про динамическое программирование", createdPost.getText());
        assertEquals(0, createdPost.getCommentsCount());
        assertEquals(0, createdPost.getLikesCount());
    }
}
