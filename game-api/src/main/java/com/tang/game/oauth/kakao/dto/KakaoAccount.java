package com.tang.game.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccount {
  @JsonProperty("profile_nickname_needs_agreement")
  private boolean profileNicknameNeedsAgreement;

  @JsonProperty("profile")
  private KakaoProfile kakaoProfile;

  @JsonProperty("has_email")
  private boolean hasEmail;

  @JsonProperty("email_needs_agreement")
  private boolean emailNeedsAgreement;

  @JsonProperty("is_email_valid")
  private boolean isEmailValid;

  @JsonProperty("is_email_verified")
  private boolean isEmailVerified;

  @JsonProperty("email")
  private String email;

  @JsonProperty("has_age_range")
  private boolean hasAgeRange;

  @JsonProperty("age_range_needs_agreement")
  private boolean ageRangeNeedsAgreement;

  @JsonProperty("age_range")
  private String ageRange;

  @JsonProperty("has_gender")
  private boolean hasGender;

  @JsonProperty("gender_needs_agreement")
  private boolean genderNeedsAgreement;

  @JsonProperty("gender")
  private String gender;
}
