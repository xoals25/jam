package com.tang.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ChatApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatApiApplication.class, args);
  }
}