package com.tang.game.user.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tang.core.type.Gender;
import com.tang.core.type.SignupPath;
import com.tang.game.WithMockCustomUser;
import com.tang.game.token.Service.TokenProvider;
import com.tang.game.token.dto.JwtTokenDto;
import com.tang.game.user.domain.User;
import com.tang.game.user.dto.SigninForm;
import com.tang.game.user.dto.SignupForm;
import com.tang.game.user.dto.UserDto;
import com.tang.game.user.repository.UserRepository;
import com.tang.game.user.service.UserService;
import com.tang.game.user.type.UserStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class})
public class UserControllerTest {

  @MockBean
  private UserService userService;

  @MockBean
  private TokenProvider tokenProvider;

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String token = "임의의 token";

  @Test
  @WithMockUser
  @DisplayName("회원가입 성공")
  void successSignUp() throws Exception {
    //given
    //when
    //then
    String body = objectMapper.writeValueAsString(getSignupForm());

    mockMvc.perform(post("/users/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
            .content(body)
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  @DisplayName("로그인 성공")
  void successLogin() throws Exception {
    //given
    LocalDateTime localDateTime = LocalDateTime.now();

    given(userService.login(any(SigninForm.class)))
        .willReturn(getJwtTokenDto(localDateTime));
    //when
    //then
    String body = objectMapper.writeValueAsString(getSigninForm());

    String ACCESS_TOKEN_EXPIRES_TIME = Date.from(localDateTime
        .plusDays(1)
        .atZone(ZoneId.systemDefault())
        .toInstant()).toString();

    String REFRESH_TOKEN_EXPIRES_TIME = Date.from(localDateTime
        .plusDays(30)
        .atZone(ZoneId.systemDefault())
        .toInstant()).toString();

    mockMvc.perform(post("/users/login")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().string("ACCESS_TOKEN", "accessToken"))
        .andExpect(header().string("REFRESH_TOKEN", "refreshToken"))
        .andExpect(header().string("ACCESS_TOKEN_EXPIRES_TIME", ACCESS_TOKEN_EXPIRES_TIME))
        .andExpect(header().string("REFRESH_TOKEN_EXPIRES_TIME", REFRESH_TOKEN_EXPIRES_TIME));
  }

  @Test
  @WithMockCustomUser
  @DisplayName("성공 회원 탈퇴")
  void successWithdrawal() throws Exception {
    //then
    mockMvc.perform(
        post("/users/withdrawal")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  @DisplayName("성공 회원 정보 불러오기")
  void successGetInfo() throws Exception {
    //given
    UserDto userDto = getUserDto();

    given(userService.getInfo(anyLong())).willReturn(userDto);

    //when
    //then
    mockMvc.perform(get("/users/getInfo")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, token)
            .with(csrf())
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("email").value(userDto.getEmail()))
        .andExpect(jsonPath("nickname").value(userDto.getNickname()))
        .andExpect(jsonPath("gender").value(userDto.getGender().toString()))
        .andExpect(jsonPath("status").value(userDto.getStatus().toString()))
        .andExpect(jsonPath("signupPath").value(userDto.getSignupPath().toString()));
  }

  private SignupForm getSignupForm() {
    return SignupForm.builder()
        .email("xoals25@naver.com")
        .nickname("nickname")
        .password("123")
        .gender(Gender.MALE)
        .ageRange("30~39")
        .build();
  }

  private SigninForm getSigninForm() {
    return SigninForm.builder()
        .email("xoals25@naver.com")
        .password("123")
        .build();
  }

  private JwtTokenDto getJwtTokenDto(LocalDateTime localDateTimeNow) {
    return JwtTokenDto.builder()
        .jwtAccessToken("accessToken")
        .jwtRefreshToken("refreshToken")
        .jwtAccessTokenExpiresTime(
            Date.from(
                localDateTimeNow
                    .plusDays(1)
                    .atZone(ZoneId.systemDefault())
                    .toInstant())
        )
        .jwtRefreshTokenExpiresTime(
            Date.from(
                localDateTimeNow
                    .plusDays(30)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()))
        .build();
  }

  private UserDto getUserDto() {
    return UserDto.builder()
        .email("xoals25@naver.com")
        .gender(Gender.MALE)
        .signupPath(SignupPath.JAM)
        .status(UserStatus.VALID)
        .nickname("엄탱")
        .build();
  }

  private User getUser() {
    return User.builder()
        .id(1L)
        .password("123")
        .email("xoals25@naver.com")
        .status(UserStatus.VALID)
        .build();
  }
}
