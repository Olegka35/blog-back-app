package com.tarasov.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class DataBasePreparator {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void prepareDatabase() {
        JdbcTemplate jdbc = namedParameterJdbcTemplate.getJdbcTemplate();

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
}
