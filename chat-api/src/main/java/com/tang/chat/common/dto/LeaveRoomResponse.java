package com.tang.chat.common.dto;

import com.tang.core.dto.LeaveRoomDto;
import com.tang.core.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRoomResponse extends LeaveRoomDto {

  private ErrorCode errorCode;

  private String message;
}
