package com.tang.chat.application;

import static org.springframework.messaging.simp.stomp.StompCommand.CONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.DISCONNECT;
import static org.springframework.messaging.simp.stomp.StompCommand.SEND;
import static org.springframework.messaging.simp.stomp.StompCommand.SUBSCRIBE;

import com.tang.chat.common.exception.JamChatException;
import com.tang.chat.token.service.TokenProvider;
import com.tang.core.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboundInterceptor implements ChannelInterceptor {

  private final TokenProvider tokenProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

    if (CONNECT == headerAccessor.getCommand()) {
      log.info("CONNECT");

    } else if (SUBSCRIBE == headerAccessor.getCommand()) {
      log.info("SUBSCRIBE");
      ckToken(headerAccessor);

    } else if (SEND == headerAccessor.getCommand()) {
      log.info("SEND");

      ckToken(headerAccessor);
    } else if (DISCONNECT == headerAccessor.getCommand()) {
      log.info("DISCONNECT");

    }

    return message;
  }

  private void ckToken(StompHeaderAccessor headerAccessor) {
    String token = headerAccessor.getFirstNativeHeader("Authorization");

    if (!tokenProvider.validateToken(token)) {
      throw new JamChatException(ErrorCode.INVALID_REQUEST);
    }
  }
}
