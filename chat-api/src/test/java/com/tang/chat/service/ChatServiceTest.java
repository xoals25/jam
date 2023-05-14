package com.tang.chat.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tang.chat.dto.ChatForm;
import com.tang.chat.type.MessageType;
import com.tang.core.repository.ParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  private ParticipantRepository participantRepository;

  @InjectMocks
  private ChatService roomService;

  @Test
  @DisplayName("성공 메시지 보내기")
  void successSendMessage() throws Exception {
    // given
    given(participantRepository.existsByRoomIdAndUserId(anyLong(), anyLong()))
        .willReturn(true);

    // when
    roomService.sendMessage(getChatFormRequest());
  }

  @Test
  @DisplayName("실패 메시지 보내기 - 방 참가자 아님")
  void failSendMessage_NOT_FOUND_ROOM_PARTICIPANT() throws Exception {
    // given
    given(participantRepository.existsByRoomIdAndUserId(anyLong(), anyLong()))
        .willReturn(true);

    // when
    roomService.sendMessage(getChatFormRequest());
  }

  private ChatForm.Request getChatFormRequest() {
    return ChatForm.Request.builder()
        .message("문자")
        .roomId(1L)
        .senderId(1L)
        .type(MessageType.MESSAGE)
        .build();
  }

}