package com.tarasov.configuration;


import org.springframework.context.annotation.*;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.tarasov.configuration",
        "com.tarasov.controller",
        "com.tarasov.service",
        "com.tarasov.repository",
        "com.tarasov.logging"
})
@PropertySource("classpath:application.properties")
@EnableAspectJAutoProxy
public class WebConfiguration {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
