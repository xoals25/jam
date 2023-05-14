package com.tang.chat.config;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboundInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

    if (Objects.equals(StompCommand.CONNECT, headerAccessor.getCommand())) {
      System.out.println("CONNECT");

    } else if (Objects.equals(StompCommand.SUBSCRIBE, headerAccessor.getCommand())) {
      System.out.println("SUBSCRIBE");

    } else if (Objects.equals(StompCommand.SEND, headerAccessor.getCommand())) {
      System.out.println("SEND");

    } else if (Objects.equals(StompCommand.DISCONNECT, headerAccessor.getCommand())) {
      System.out.println("DISCONNECT");
    }

    return message;
  }
}
