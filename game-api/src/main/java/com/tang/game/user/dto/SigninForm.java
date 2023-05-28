package com.tang.game.user.dto;

import javax.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigninForm {

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;
}
