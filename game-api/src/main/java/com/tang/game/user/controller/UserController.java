package com.tang.game.user.controller;

import static com.tang.game.token.constant.TokenResponseConstant.tokenResponse;

import com.tang.game.user.domain.User;
import com.tang.game.user.dto.SigninForm;
import com.tang.game.user.dto.SignupForm;
import com.tang.game.user.dto.UserDto;
import com.tang.game.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public void signup(@RequestBody @Valid SignupForm form) {
    userService.signup(form);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid SigninForm form) {
    return tokenResponse(userService.login(form));
  }

  @PostMapping("/withdrawal")
  public void withdrawal(@AuthenticationPrincipal User user) {
    userService.withdrawal(user.getId());
  }

  @GetMapping("/getInfo")
  public ResponseEntity<UserDto> getInfo(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(userService.getInfo(user.getId()));
  }

  @GetMapping("/ckEmailOverLap")
  public ResponseEntity<Boolean> ckEmailOverLap(@RequestParam String email) {
    return ResponseEntity.ok(userService.ckEmailOverLap(email));
  }
}
