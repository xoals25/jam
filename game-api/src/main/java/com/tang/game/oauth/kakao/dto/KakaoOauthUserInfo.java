package com.tang.game.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class KakaoOauthUserInfo {
  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Response {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;
  }
}
