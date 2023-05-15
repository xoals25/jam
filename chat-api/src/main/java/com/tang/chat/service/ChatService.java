package com.tang.chat.service;

import com.tang.chat.common.exception.JamChatException;
import com.tang.chat.common.type.ErrorCode;
import com.tang.chat.dto.ChatForm;
import com.tang.core.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final SimpMessagingTemplate simpMessagingTemplate;

  private final ParticipantRepository participantRepository;

  public void sendMessage(ChatForm.Request request) {
    if (!participantRepository.existsByRoomIdAndUserId(
        request.getRoomId(), request.getSenderId())) {
      throw new JamChatException(ErrorCode.NOT_FOUND_ROOM_PARTICIPANT);
    }

    simpMessagingTemplate.convertAndSend(
        "/rooms/" + request.getRoomId(),
        request
    );
  }
}
