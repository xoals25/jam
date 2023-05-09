package com.tang.game.common.type;

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
  EXIST_ROOM_TITLE("중복된 방 제목입니다.");

  private final String description;
}
