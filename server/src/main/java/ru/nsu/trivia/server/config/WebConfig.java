package ru.nsu.trivia.server.config;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    HttpTraceRepository httpTraceRepository(){
        return new InMemoryHttpTraceRepository();
    }
}
