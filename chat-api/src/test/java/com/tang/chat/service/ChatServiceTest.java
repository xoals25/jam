package com.tang.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tang.chat.client.GameClient;
import com.tang.chat.common.exception.JamChatException;
import com.tang.chat.common.type.ErrorCode;
import com.tang.chat.dto.ChatDto;
import com.tang.chat.dto.ChatForm;
import com.tang.chat.type.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @InjectMocks
  private ChatService roomService;

  @Mock
  private SimpMessagingTemplate simpMessagingTemplate;

  @Mock
  private GameClient gameClient;

  @Test
  @DisplayName("성공 메시지 보내기")
  void successSendMessage() throws Exception {
    // given
    given(gameClient.isRoomParticipant(anyLong(), anyLong()))
        .willReturn(ResponseEntity.ok(Boolean.TRUE));

    // when
    roomService.sendMessage(getChatDto());
  }

  @Test
  @DisplayName("실패 메시지 보내기 - 방 참가자 아님")
  void failSendMessage_NOT_FOUND_ROOM_PARTICIPANT() throws Exception {
    // given
    given(gameClient.isRoomParticipant(anyLong(), anyLong()))
        .willReturn(ResponseEntity.ok(Boolean.FALSE));

    //when
    JamChatException exception = assertThrows(JamChatException.class,
        () -> roomService.sendMessage(getChatDto()));

    // then
    assertEquals(ErrorCode.NOT_FOUND_ROOM_PARTICIPANT, exception.getErrorCode());
  }

  private ChatDto getChatDto() {
    return ChatDto.from(getChatFormRequest());
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