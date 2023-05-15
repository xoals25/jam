package com.tang.chat.dto;

import com.tang.chat.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
  private MessageType type;
  private Long senderId;
  private Long roomId;
  private String message;

  public static ChatDto from(ChatForm.Request requestForm) {
    return ChatDto.builder()
        .type(requestForm.getType())
        .senderId(requestForm.getSenderId())
        .roomId(requestForm.getRoomId())
        .message(requestForm.getMessage())
        .build();
  }
}
