package com.tang.game.token.domain;

import com.tang.core.domain.BaseEntity;
import com.tang.game.token.dto.JwtTokenDto;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
public class Token extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private Long userId;

  private String jwtGrantType;

  private String jwtAccessToken;

  private String jwtRefreshToken;

  private String oauthAccessToken;

  public static Token of(Long userId, JwtTokenDto jwtTokenDto, String oauthAccessToken) {
    return Token.builder()
        .userId(userId)
        .jwtAccessToken(jwtTokenDto.getJwtAccessToken())
        .jwtGrantType(jwtTokenDto.getGrantType())
        .jwtRefreshToken(jwtTokenDto.getJwtRefreshToken())
        .oauthAccessToken(oauthAccessToken)
        .build();
  }
}
