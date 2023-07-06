package com.tang.chat.dto;

import com.tang.core.dto.ParticipantResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatEnterDto extends ChatDto {

  private ParticipantResponse participantResponse;

  public static ChatEnterDto of(
      ParticipantResponse participantResponse,
      ChatDto chatDto
  ) {
    ChatEnterDto chatEnterDto = new ChatEnterDto(participantResponse);

    chatEnterDto.setChatDto(chatDto);

    return chatEnterDto;
  }
}
