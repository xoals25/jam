package com.tang.game.token.Service;

import static com.tang.game.token.utils.Constants.KEY_ROLES;
import static com.tang.game.token.utils.Constants.TOKEN_PREFIX;

import com.tang.game.token.dto.JwtTokenDto;
import com.tang.game.user.domain.User;
import com.tang.game.user.service.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1 hour

  private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 30L; //30일

  private final CustomUserDetailService customUserDetailService;

  @Value("${spring.jwt.secret}")
  private String secretKey;

  public JwtTokenDto generateToken(
      Long userId,
      List<String> roles
  ) {
    Claims claims = Jwts.claims()
        .setSubject(userId.toString());

    claims.put(KEY_ROLES, roles);

    Date now = new Date();
    Date accessTokenExpiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
    Date refreshTokenExpiredDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

    return JwtTokenDto.builder()
        .grantType(TOKEN_PREFIX)
        .jwtAccessToken(getToken(claims, now, accessTokenExpiredDate))
        .jwtRefreshToken(getToken(claims, now, refreshTokenExpiredDate))
        .jwtAccessTokenExpiresTime(accessTokenExpiredDate)
        .jwtRefreshTokenExpiresTime(refreshTokenExpiredDate)
        .build();
  }

  public String newGenerateAccessToken(User user) {
    Claims claims = Jwts.claims()
        .setSubject(user.getSignupPath() + "&&" + user.getEmail())
        .setId(user.getId().toString());
    claims.put(KEY_ROLES, Collections.singletonList(user.getStatus().getKey()));

    Date now = new Date();
    Date accessTokenExpiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

    return getToken(claims, now, accessTokenExpiredDate);
  }

  private String getToken(Claims claims, Date now, Date tokenExpiredDate) {
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(tokenExpiredDate)
        .signWith(SignatureAlgorithm.HS512, this.secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails =
        customUserDetailService.loadUserByUsername(parseClaims(token).getSubject());

    return new UsernamePasswordAuthenticationToken(
        userDetails,
        "",
        userDetails.getAuthorities()
    );
  }

  public String getSignupPathAndEmail(String token) {
    return this.parseClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    return !this.parseClaims(token)
        .getExpiration()
        .before(new Date());
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

}
