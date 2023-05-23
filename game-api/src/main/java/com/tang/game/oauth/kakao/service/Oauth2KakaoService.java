package com.tang.game.oauth.kakao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.core.type.Gender;
import com.tang.core.type.SignupPath;
import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.token.domain.Token;
import com.tang.game.token.dto.JwtTokenDto;
import com.tang.game.token.repository.TokenRepository;
import com.tang.game.token.Service.TokenProvider;
import com.tang.game.user.domain.User;
import com.tang.game.user.repository.UserRepository;
import com.tang.game.user.type.UserStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

  @Transactional
  public JwtTokenDto login(String code) {
    JSONObject kakaoTokenAndScopeJSONData = getKakaoTokenAndScopeJSONData(code);

    User user = parseUserForKakaoResponse(
        getKakaoOauthInfoJSONData(kakaoTokenAndScopeJSONData)
    );

    userRepository.save(user);

    JwtTokenDto jwtTokenDto = tokenProvider.generateToken(
        user.getEmail(),
        user.getSignupPath(),
        user.getId(),
        Collections.singletonList(user.getStatus().getKey())
    );

    String accessToken = kakaoTokenAndScopeJSONData.get("access_token").toString();

    tokenRepository.findByUserId(user.getId())
        .ifPresentOrElse(
            token -> {
              token.setOauthAccessToken(accessToken);
              token.setJwtAccessToken(jwtTokenDto.getJwtAccessToken());
              token.setJwtRefreshToken(jwtTokenDto.getJwtRefreshToken());
            },
            () -> tokenRepository.save(Token.of(user.getId(), jwtTokenDto, accessToken))
        );

    return jwtTokenDto;
  }

  private User parseUserForKakaoResponse(JSONObject kakaoOauthInfoJSONData) {
    String nickname = null;
    String email = null;
    String ageRange = null;
    Gender gender = null;

    JSONParser jsonParser = new JSONParser();
    ObjectMapper mapper = new ObjectMapper();

    JSONObject kakaoResponseJson = null;

    try {
      kakaoResponseJson = (JSONObject) jsonParser.parse(
          mapper.writer().writeValueAsString(kakaoOauthInfoJSONData.get("kakao_account"))
      );
    } catch (ParseException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    boolean isNickNameAgreement = !(Boolean) kakaoResponseJson
        .get("profile_nickname_needs_agreement");

    boolean isEmailAgreement = !(Boolean) kakaoResponseJson
        .get("email_needs_agreement");

    boolean isAgeRangeAgreement = !(Boolean) kakaoResponseJson
        .get("age_range_needs_agreement");

    boolean isGenderAgreement = !(Boolean) kakaoResponseJson
        .get("gender_needs_agreement");

    if (isNickNameAgreement) {
      nickname = ((JSONObject) kakaoResponseJson.get("profile"))
          .get("nickname").toString();
    }

    if (isEmailAgreement) {
      email = kakaoResponseJson.get("email").toString();

      Optional<User> user =
          userRepository.findByEmailAndSignupPath(email, SignupPath.KAKAO);

      if (user.isPresent()) {
        if (user.get().getStatus() == UserStatus.VALID) {
          return user.get();
        } else if (!user.get().getDeletedAt().isBefore(LocalDateTime.now().minusDays(7))) {
          throw new JamGameException(ErrorCode.DELETE_YET_REMAIN_7DAYS);
        }
      }
    } else {
      unlink(kakaoResponseJson.get("access_token").toString());
      throw new JamGameException(ErrorCode.OAUTH_SING_UP_REQUIRE_EMAIL);
    }

    if (isAgeRangeAgreement) {
      ageRange = kakaoResponseJson.get("age_range").toString();
    }

    if (isGenderAgreement) {
      gender = Objects.equals("male", kakaoResponseJson.get("gender")) ?
          Gender.MALE : Gender.FEMALE;
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

  private JSONObject getKakaoTokenAndScopeJSONData(String code) {
    WebClient webClient = WebClient.builder()
        .baseUrl(tokenUrl)
        .defaultHeader(HttpHeaders.CONTENT_TYPE,
            "application/x-www-form-urlencoded;charset=utf-8")
        .build();

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    params.add("grant_type", grantType);
    params.add("client_id", clientId);
    params.add("redirect_uri", redirectUrl);
    params.add("code", code);

    return webClient
        .post()
        .bodyValue(params)
        .retrieve()
        .bodyToMono(JSONObject.class)
        .block();
  }

  private JSONObject getKakaoOauthInfoJSONData(JSONObject kakaoTokenAndScopeJSONData) {
    String accessToken = kakaoTokenAndScopeJSONData.get("access_token").toString();

    System.out.println(accessToken);
    WebClient webClient = WebClient.builder()
        .baseUrl(userInfoUrl)
        .defaultHeaders(headers -> {
          headers.add(HttpHeaders.CONTENT_TYPE,
              "application/x-www-form-urlencoded;charset=utf-8");
          headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        })
        .build();

    return webClient
        .post()
        .retrieve()
        .bodyToMono(JSONObject.class)
        .block();
  }

  public void logout(Long userId) {
    webClientPostWithAuthorization("https://kapi.kakao.com/v1/user/logout",
        "Bearer " + getOauthAccessToken(userId));
  }

  public void unlink(Long userId) {
    unlink(getOauthAccessToken(userId));
  }

  private void unlink(String token) {
    webClientPostWithAuthorization("https://kapi.kakao.com/v1/user/unlink",
        "Bearer " + token);
  }

  private String getOauthAccessToken(Long userId) {
    Token token = tokenRepository.findByUserId(userId)
        .orElseThrow(() -> new JamGameException(ErrorCode.USER_NOT_FOUND));

    return token.getOauthAccessToken();
  }

  private void webClientPostWithAuthorization(String url, String authorization) {
    WebClient.builder()
        .baseUrl("https://kapi.kakao.com/v1/user/unlink")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()
        .post()
        .header(HttpHeaders.AUTHORIZATION, authorization)
        .retrieve()
        .bodyToMono(JSONObject.class)
        .block();
  }
}
