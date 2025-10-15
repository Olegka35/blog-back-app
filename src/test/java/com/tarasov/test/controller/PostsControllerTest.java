package com.tarasov.test.controller;

import com.tarasov.test.DataBasePreparator;
import com.tarasov.test.configuration.WebConfiguration;
import com.tarasov.test.configuration.DataSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Arrays;
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
    private DataBasePreparator dataBasePreparator;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        dataBasePreparator.prepareDatabase();
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

    @Test
    void createPostTest() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "New title",
                                "text": "New content",
                                "tags": ["tag1"]
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.text").value("New content"));
    }
}
