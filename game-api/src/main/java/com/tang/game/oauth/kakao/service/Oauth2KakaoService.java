package com.tang.game.oauth.kakao.service;

import static com.tang.game.token.utils.Constants.TOKEN_PREFIX;

import com.tang.core.type.Gender;
import com.tang.core.type.SignupPath;
import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.oauth.kakao.dto.KakaoAccount;
import com.tang.game.oauth.kakao.dto.KakaoOauthTokenAndScope;
import com.tang.game.oauth.kakao.dto.KakaoOauthUserInfo;
import com.tang.game.oauth.kakao.dto.KakaoProfile;
import com.tang.game.token.Service.TokenProvider;
import com.tang.game.token.domain.Token;
import com.tang.game.token.dto.JwtTokenDto;
import com.tang.game.token.repository.TokenRepository;
import com.tang.game.user.domain.User;
import com.tang.game.user.repository.UserRepository;
import com.tang.game.user.type.UserStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class Oauth2KakaoService {

  @Value(value = "${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
  private String grantType;

  @Value(value = "${spring.security.oauth2.client.registration.kakao.client-id}")
  private String clientId;

  @Value(value = "${spring.security.oauth2.client.registration.kakao.redirect-uri}")
  private String redirectUrl;

  @Value(value = "${spring.security.oauth2.client.provider.kakao.token_uri}")
  private String tokenUrl;

  @Value(value = "${spring.security.oauth2.client.provider.kakao.user-info-uri}")
  private String userInfoUrl;

  private final TokenRepository tokenRepository;

  private final UserRepository userRepository;

  private final TokenProvider tokenProvider;

  private final WebClient webClient;

  @Transactional
  public JwtTokenDto login(String code) {
    KakaoOauthTokenAndScope.Response kakaoTokenAndScope = getKakaoTokenAndScope(code);

    User user = parseUserForKakaoResponse(
        getKakaoOauthInfoJSONData(kakaoTokenAndScope),
        kakaoTokenAndScope.getAccessToken()
    );

    userRepository.save(user);

    JwtTokenDto jwtTokenDto = tokenProvider.generateToken(
        user.getEmail(),
        user.getSignupPath(),
        user.getId(),
        Collections.singletonList(user.getStatus().getKey())
    );

    String accessToken = kakaoTokenAndScope.getAccessToken();

    tokenRepository.findByUserId(user.getId())
        .ifPresentOrElse(
            token -> {
              token.setOauthAccessToken(accessToken);
              token.setJwtAccessToken(jwtTokenDto.getJwtAccessToken());
              token.setJwtRefreshToken(jwtTokenDto.getJwtRefreshToken());
            },
            () -> tokenRepository.save(Token.of(user.getId(), jwtTokenDto, accessToken)));

    return jwtTokenDto;
  }

  private User parseUserForKakaoResponse(
      KakaoOauthUserInfo.Response kakaoOauthUserInfo,
      String accessToken
  ) {
    KakaoAccount kakaoAccount = kakaoOauthUserInfo.getKakaoAccount();

    String nickname = Optional.ofNullable(kakaoAccount.getKakaoProfile())
        .map(KakaoProfile::getNickname)
        .orElse(null);

    String ageRange = kakaoAccount.getAgeRange();

    Gender gender = Optional.ofNullable(kakaoAccount.getGender())
        .map(it -> Objects.equals("male", it) ? Gender.MALE : Gender.FEMALE)
        .orElse(null);

    String email = Optional.ofNullable(kakaoAccount.getEmail())
        .orElseThrow(() -> {
          unlink(accessToken);
          throw new JamGameException(ErrorCode.OAUTH_SING_UP_REQUIRE_EMAIL);
        });

    Optional<User> user = userRepository.findByEmailAndSignupPath(email, SignupPath.KAKAO);

    if (user.isPresent()) {
      if (user.get().getStatus() == UserStatus.VALID) {
        return user.get();
      } else if (!user.get().getDeletedAt().isBefore(LocalDateTime.now().minusDays(7))) {
        throw new JamGameException(ErrorCode.DELETE_YET_REMAIN_7DAYS);
      }
    }

    return User.builder()
        .email(email)
        .nickname(nickname)
        .gender(gender)
        .ageRange(ageRange)
        .status(UserStatus.VALID)
        .signupPath(SignupPath.KAKAO)
        .build();
  }

  private KakaoOauthTokenAndScope.Response getKakaoTokenAndScope(String code) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("grant_type", grantType);
    params.add("client_id", clientId);
    params.add("redirect_uri", redirectUrl);
    params.add("code", code);

    return webClient
        .post()
        .uri(tokenUrl)
        .header(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded;charset=utf-8")
        .bodyValue(params)
        .retrieve()
        .bodyToMono(KakaoOauthTokenAndScope.Response.class)
        .block();
  }

  private KakaoOauthUserInfo.Response getKakaoOauthInfoJSONData(
      KakaoOauthTokenAndScope.Response KakaoOauthTokenAndScope
  ) {
    String accessToken = KakaoOauthTokenAndScope.getAccessToken();

    return webClient
        .post()
        .uri(userInfoUrl)
        .headers(
            httpHeaders -> {
              httpHeaders.set(HttpHeaders.CONTENT_TYPE,
                  "application/x-www-form-urlencoded;charset=utf-8");
              httpHeaders.add(HttpHeaders.AUTHORIZATION,
                  TOKEN_PREFIX + accessToken);
            }
        )
        .retrieve()
        .bodyToMono(KakaoOauthUserInfo.Response.class)
        .block();
  }

  public void logout(Long userId) {
    webClientPostWithAuthorization("https://kapi.kakao.com/v1/user/logout",
        TOKEN_PREFIX + getOauthAccessToken(userId));
  }

  @Transactional
  public void unlink(Long userId) {
    unlink(getOauthAccessToken(userId));
  }

  private void unlink(String token) {
    webClientPostWithAuthorization("https://kapi.kakao.com/v1/user/unlink",
        TOKEN_PREFIX + token);
  }

  private String getOauthAccessToken(Long userId) {
    Token token = tokenRepository.findByUserId(userId)
        .orElseThrow(() -> new JamGameException(ErrorCode.USER_NOT_FOUND));

    return token.getOauthAccessToken();
  }

  private void webClientPostWithAuthorization(String url, String authorization) {
    webClient
        .post()
        .uri(url)
        .headers(httpHeaders -> {
          httpHeaders.set(HttpHeaders.CONTENT_TYPE,
              MediaType.APPLICATION_FORM_URLENCODED_VALUE);
          httpHeaders.add(HttpHeaders.AUTHORIZATION, authorization);
        })
        .retrieve()
        .bodyToMono(void.class)
        .block();
  }
}
