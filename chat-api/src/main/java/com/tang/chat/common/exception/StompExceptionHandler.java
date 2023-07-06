package com.tang.chat.common.exception;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

  @Override
  public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
      Throwable ex) {

    if (ex.getCause() instanceof JamChatException) {
      return prepareErrorMessage((JamChatException)ex.getCause());
    }

    return super.handleClientMessageProcessingError(clientMessage, ex);
  }

  @Override
  protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor,
      byte[] errorPayload, Throwable cause, StompHeaderAccessor clientHeaderAccessor) {

    return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
  }

  private Message<byte[]> prepareErrorMessage(JamChatException ex) {

    final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
    accessor.setMessage(ex.getMessage());
    accessor.setLeaveMutable(true);

    return MessageBuilder.createMessage(
        ex.toString().getBytes(StandardCharsets.UTF_8),
        accessor.getMessageHeaders()
    );
  }
}
