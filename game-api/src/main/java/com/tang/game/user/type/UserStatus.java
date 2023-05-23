package com.tang.game.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
  VALID("VALID", "일반 회원"),
  DELETE("DELETE", "탈퇴 회원");

  private final String key;
  private final String status;
}
