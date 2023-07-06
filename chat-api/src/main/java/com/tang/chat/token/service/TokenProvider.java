package com.tang.chat.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    return !this.parseClaims(token)
        .getExpiration()
        .before(new Date());
  }

  public long getUserId(String token) {
    return Long.parseLong(this.parseClaims(token).getSubject());
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

}
