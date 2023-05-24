package com.tang.core.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SignupPath {
  KAKAO("kakao"),
  JAM("jam");

  private final String value;
}
