package com.tang.game.user.dto;

import com.tang.core.type.Gender;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupForm {
  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String ageRange;
}
