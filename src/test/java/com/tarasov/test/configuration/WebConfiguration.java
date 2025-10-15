package com.tarasov.test.configuration;

import com.tarasov.test.DataBasePreparator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.tarasov.controller",
        "com.tarasov.service",
        "com.tarasov.repository",
        "com.tarasov.logging"
})
public class WebConfiguration {

    @Bean
    public DataBasePreparator dataBasePreparator() {
        return new DataBasePreparator();
    }
}