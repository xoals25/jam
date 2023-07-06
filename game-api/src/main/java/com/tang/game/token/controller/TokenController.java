package com.tang.game.token.controller;

import com.tang.game.token.Service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  @GetMapping("/refresh")
  public ResponseEntity<?> refreshToken(
      @RequestHeader(name = "ACCESS_TOKEN") String accessToken,
      @RequestHeader(name = "REFRESH_TOKEN") String refreshToken
  ) {
    return ResponseEntity.ok().header(
        "ACCESS_TOKEN",
        tokenService.tokenRefresh(accessToken, refreshToken)
    ).build();
  }
}
