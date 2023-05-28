package com.tang.game.token.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtTokenDto {

  private String grantType;

  private String jwtAccessToken;

  private String jwtRefreshToken;

  private Date jwtAccessTokenExpiresTime;

  private Date jwtRefreshTokenExpiresTime;
}

