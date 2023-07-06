package com.tang.chat.dto;

import com.tang.core.dto.LeaveRoomDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatLeaveDto extends ChatDto {

  private LeaveRoomDto leaveRoomDto;

  public static ChatLeaveDto of(
      LeaveRoomDto leaveRoomDto,
      ChatDto chatDto
  ) {
    ChatLeaveDto chatLeaveDto = new ChatLeaveDto(leaveRoomDto);

    chatLeaveDto.setChatDto(chatDto);

    return chatLeaveDto;
  }
}
