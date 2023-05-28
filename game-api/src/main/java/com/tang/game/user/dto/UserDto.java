package com.tang.game.user.dto;

import com.tang.core.type.Gender;
import com.tang.core.type.SignupPath;
import com.tang.game.user.domain.User;
import com.tang.game.user.type.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
  private String email;
  private String nickname;
  private Gender gender;
  private UserStatus status;
  private SignupPath signupPath;

  public static UserDto from(User user) {
    return UserDto.builder()
        .email(user.getEmail())
        .nickname(user.getNickname())
        .gender(user.getGender())
        .status(user.getStatus())
        .signupPath(user.getSignupPath())
        .build();
  }
}
