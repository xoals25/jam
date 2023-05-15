package com.tang.chat.service;

import com.tang.chat.client.GameClient;
import com.tang.chat.common.exception.JamChatException;
import com.tang.chat.common.type.ErrorCode;
import com.tang.chat.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final GameClient gameClient;

  public void sendMessage(ChatDto chatDto) {
    if (Boolean.FALSE.equals(gameClient.isRoomParticipant(
            chatDto.getRoomId(),
            chatDto.getSenderId())
        .getBody())) {
      throw new JamChatException(ErrorCode.NOT_FOUND_ROOM_PARTICIPANT);
    }

    simpMessagingTemplate.convertAndSend(
        "/rooms/" + chatDto.getRoomId(),
        chatDto
    );
  }
}
