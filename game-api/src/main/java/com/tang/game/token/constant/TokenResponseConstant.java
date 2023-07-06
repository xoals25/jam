package com.tang.game.token.constant;

import com.tang.game.token.dto.JwtTokenDto;
import org.springframework.http.ResponseEntity;

public class TokenResponseConstant {
  public static ResponseEntity<?> tokenResponse(JwtTokenDto jwtTokenDto) {
    return ResponseEntity.ok()
        .headers(header -> {
          header.add("TOKEN_GRANT_TYPE", jwtTokenDto.getGrantType());
          header.add("ACCESS_TOKEN", jwtTokenDto.getJwtAccessToken());
          header.add("REFRESH_TOKEN", jwtTokenDto.getJwtRefreshToken());
          header.add("ACCESS_TOKEN_EXPIRES_TIME",
              String.valueOf(jwtTokenDto.getJwtAccessTokenExpiresTime()));
          header.add("REFRESH_TOKEN_EXPIRES_TIME",
              String.valueOf(jwtTokenDto.getJwtRefreshTokenExpiresTime()));
        })
        .build();
  }
}
