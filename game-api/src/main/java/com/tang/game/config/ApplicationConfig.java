package com.tang.game.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApplicationConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE,
            MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
