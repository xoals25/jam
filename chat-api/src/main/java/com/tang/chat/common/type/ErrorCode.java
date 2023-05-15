package com.tang.chat.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // common
  INTERNAL_SERVER_ERROR("내부 서버 오류가 발생 했습니다."),
  INVALID_REQUEST("잘못된 요청입니다."),
  USER_NOT_FOUND("사용자가 없습니다."),

  // Room
  NOT_FOUND_ROOM_PARTICIPANT("방 참가자가 아닙니다");

  private final String description;
}
