package com.tang.chat.controller;

import com.tang.chat.common.dto.DisconnectNeedInfo;
import com.tang.chat.dto.ChatDto;
import com.tang.chat.dto.ChatForm;
import com.tang.chat.service.ChatService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  // Map<sessionId, participantId>
  private final Map<String, DisconnectNeedInfo> map = new HashMap<>();

  @MessageMapping("/message")
  public void message(
      @Payload Message<?> message,
      ChatForm.Request request) {

    chatService.sendMessage(
        StompHeaderAccessor.wrap(message),
        ChatDto.from(request)
    );
  }

  @SubscribeMapping("/rooms/{roomId}")
  public void subscribe(
      @Payload Message<?> message,
      @DestinationVariable Long roomId
  ) {

    chatService.enterRoom(
        StompHeaderAccessor.wrap(message),
        roomId,
        map
    );
  }

  @MessageExceptionHandler
  public void handleException(
      @Payload Message<?> message,
      @Header("roomId") long roomId,
      Exception exception) {

    chatService.sendError(
        (String) message.getHeaders().get("simpSessionId"),
        roomId,
        exception
    );
  }

  @EventListener(SessionDisconnectEvent.class)
  public void onDisconnect(SessionDisconnectEvent event) {

    chatService.leaveRoom(
        event.getSessionId(),
        map
    );
  }
}
