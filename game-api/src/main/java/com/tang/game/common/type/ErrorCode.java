package com.tang.game.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // common
  INTERNAL_SERVER_ERROR("내부 서버 오류가 발생 했습니다."),
  INVALID_REQUEST("잘못된 요청입니다."),
  USER_NOT_FOUND("존재하지 않는 사용자입니다."),

  // User
  EMAIL_OR_PASSWORD_UN_MATCH("아이디 혹은 비밀번호를 다시 확인해주세요"),
  PASSWORD_UN_MATCH("비밀번호가 일치하지 않습니다."),
  ALREADY_EXIST_EMAIL("이미 존재하는 이메일입니다."),

  // Room
  USER_ROOM_HOST_UN_MATCH("게임방 주인이 아닙니다."),
  NOT_FOUND_ROOM("존재하지 않는 방입니다."),
  EXIST_ROOM_TITLE("중복된 방 제목입니다."),
  UN_MATCH_CREATE_ROOM_USER_ID_AND_LOGIN_USER_ID("방을 만든 유저와 로그인한 유저가 동일하지 않습니다."),
  LIMIT_PARTICIPANT_COUNT_NOT_MIN_CURRENT_PARTICIPANT_COUNT("방 제한 인원은 현재 참여 인원 보다 작을 수 없습니다."),

  // jwt
  NOT_FOUND_TOKEN_MATCHING_USER("해당 유저에 매칭되는 토큰을 찾을 수 없습니다."),
  EXPIRE_REFRESH_TOKEN("기간이 만료된 토큰입니다."),
  NOT_FOUND_REFRESH_TOKEN("존재하지 않는 토큰입니다."),

  // oauth
  OAUTH_SING_UP_REQUIRE_EMAIL("이메일 동의가 반드시 필요합니다."),
  DELETE_YET_REMAIN_7DAYS("탈퇴 한지 7일을 넘지 않았습니다. (탈퇴 철회 필요)"),

  ;

  private final String description;
}