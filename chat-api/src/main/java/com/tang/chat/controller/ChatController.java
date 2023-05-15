package com.tang.chat.controller;

import com.tang.chat.dto.ChatDto;
import com.tang.chat.dto.ChatForm;
import com.tang.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  @MessageMapping("/message")
  public void message(ChatForm.Request request) {
    chatService.sendMessage(ChatDto.from(request));
  }
}
