package com.tang.game.oauth.kakao.controller;

import com.tang.game.oauth.kakao.service.Oauth2KakaoService;
import com.tang.game.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/kakao")
public class Oauth2KakaoController {

  private final Oauth2KakaoService oauth2KakaoService;

  @GetMapping("/code")
  public ResponseEntity<String> getCode(@RequestParam String code) {
    return ResponseEntity.ok(code);
  }

  @GetMapping("/login")
  public ResponseEntity<?> login(@RequestParam String code) {
    return ResponseEntity.ok()
        .header(HttpHeaders.AUTHORIZATION, oauth2KakaoService.login(code).toString())
        .build();
  }

  @GetMapping("/logout")
  public void logout(@AuthenticationPrincipal User user) {
    oauth2KakaoService.logout(user.getId());
  }

  @GetMapping("/unlink")
  public void unlink(@AuthenticationPrincipal User user) {
    oauth2KakaoService.unlink(user.getId());
  }
}
