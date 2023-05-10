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
  USER_ROOM_HOST_UN_MATCH("게임방 주인이 아닙니다."),
  NOT_FOUND_ROOM("존재하지 않는 방입니다."),
  EXIST_ROOM_TITLE("중복된 방 제목입니다.");

  private final String description;
}
