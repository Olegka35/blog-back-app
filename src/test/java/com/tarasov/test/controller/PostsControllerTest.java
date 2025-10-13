package com.tarasov.test.controller;


import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.test.configuration.WebConfiguration;
import com.tarasov.test.configuration.DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource("classpath:test-application.properties")
public class PostsControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        JdbcTemplate jdbc = jdbcTemplate.getJdbcTemplate();

        jdbc.execute("DELETE FROM comments");
        jdbc.execute("DELETE FROM tags");
        jdbc.execute("DELETE FROM posts");
        jdbc.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
        jdbc.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");

        jdbc.execute("""
            INSERT INTO posts (title, text, likes_count)
            VALUES
              ('Первый пост', 'Это текст первого поста. Добро пожаловать!', 10),
              ('Spring Boot + H2 пост', 'Демонстрация интеграции H2 с Spring Boot.', 25),
              ('Фотоотчёт с конференции', 'Краткий обзор конференции JavaDay.', 40),
              ('Реактивное программирование', 'Обзор Project Reactor и WebFlux.', 15),
              ('Итоги 2025 года', 'Что изменилось в Java мире за год?', 5);
        """);

        jdbc.execute("""
            INSERT INTO tags (post_id, tag)
            VALUES
              (1, 'введение'),
              (1, 'новости'),
              (2, 'spring'),
              (2, 'h2'),
              (3, 'java'),
              (3, 'конференция'),
              (4, 'reactive'),
              (4, 'webflux'),
              (5, 'java'),
              (5, 'итоги');
        """);

        jdbc.execute("""
            INSERT INTO comments (post_id, text)
            VALUES
              (1, 'Отличный старт!'),
              (1, 'Ждём новых постов.'),
              (2, 'Спасибо за пример, всё работает.'),
              (2, 'Можно ли сделать то же самое с PostgreSQL?'),
              (3, 'Было здорово!'),
              (4, 'Отличное объяснение WebFlux.'),
              (4, 'Добавьте больше примеров.'),
              (5, 'Хорошая аналитика!'),
              (5, 'С нетерпением жду 2026.');
        """);
    }

    @Test
    void getPostTest() throws Exception {
        mockMvc.perform(get("/posts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.likesCount").value(25));
    }

    @Test
    void getPostNotFoundTest() throws Exception {
        mockMvc.perform(get("/posts/20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchPostsTest() throws Exception {
        mockMvc.perform(get("/posts")
                        .param("search", "пост #h2")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].title").value("Spring Boot + H2 пост"))
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void deletePostTest() throws Exception {
        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePostNotFoundTest() throws Exception {
        mockMvc.perform(delete("/posts/100"))
                .andExpect(status().isNotFound());
    }
}
