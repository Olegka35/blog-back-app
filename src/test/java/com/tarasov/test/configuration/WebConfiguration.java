package com.tarasov.test.configuration;

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
}