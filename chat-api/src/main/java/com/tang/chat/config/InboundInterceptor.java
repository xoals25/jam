package com.tang.chat.config;

import static org.springframework.messaging.simp.stomp.StompCommand.*;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboundInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

    if (CONNECT == headerAccessor.getCommand()) {
      log.info("CONNECT");

    } else if (SUBSCRIBE == headerAccessor.getCommand()) {
      log.info("SUBSCRIBE");

    } else if (SEND == headerAccessor.getCommand()) {
      log.info("SEND");

    } else if (DISCONNECT == headerAccessor.getCommand()) {
      log.info("DISCONNECT");

    }

    return message;
  }
}
