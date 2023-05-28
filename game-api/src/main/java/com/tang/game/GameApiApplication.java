package com.tang.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GameApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(GameApiApplication.class, args);
  }
}