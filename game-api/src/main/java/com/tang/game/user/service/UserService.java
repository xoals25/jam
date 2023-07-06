package com.tang.game.user.service;

import static com.tang.core.type.SignupPath.JAM;
import static com.tang.game.user.type.UserStatus.VALID;

import com.tang.game.common.exception.JamGameException;
import com.tang.core.type.ErrorCode;
import com.tang.game.token.service.TokenProvider;
import com.tang.game.token.domain.Token;
import com.tang.game.token.dto.JwtTokenDto;
import com.tang.game.token.repository.TokenRepository;
import com.tang.game.user.domain.User;
import com.tang.game.user.dto.SigninForm;
import com.tang.game.user.dto.SignupForm;
import com.tang.game.user.dto.UserDto;
import com.tang.game.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final TokenProvider tokenProvider;

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final PasswordEncoder bCryptPasswordEncoder;

  public void signup(SignupForm form) {
    if (userRepository.existsByEmailAndDeletedAtNullOrDeletedAtBefore(
        form.getEmail(), LocalDateTime.now().minusDays(7))) {
      throw new JamGameException(ErrorCode.ALREADY_EXIST_EMAIL);
    }

    userRepository.save(User.of(form, bCryptPasswordEncoder));
  }

  @Transactional
  public JwtTokenDto login(SigninForm form) {
    User user = userRepository.findByEmailAndSignupPathAndStatus(form.getEmail(), JAM, VALID)
        .filter(it -> bCryptPasswordEncoder.matches(form.getPassword(), it.getPassword()))
        .orElseThrow(() -> new JamGameException(ErrorCode.EMAIL_OR_PASSWORD_UN_MATCH));

    JwtTokenDto jwtTokenDto = tokenProvider.generateToken(
        user.getId(),
        Collections.singletonList(user.getStatus().getKey())
    );

    tokenRepository.findByUserId(user.getId()).ifPresentOrElse(
        (token) -> {
          token.setJwtAccessToken(jwtTokenDto.getJwtAccessToken());
          token.setJwtRefreshToken(jwtTokenDto.getJwtRefreshToken());
        },
        () ->  tokenRepository.save(Token.of(user.getId(), jwtTokenDto))
    );

    return jwtTokenDto;
  }

  public boolean ckEmailOverLap(String email) {
    return userRepository.existsByEmailAndStatus(email, VALID);
  }

  @Transactional
  public void withdrawal(Long userId) {
    userRepository.delete(findByUserId(userId));
  }

  public UserDto getInfo(Long userId) {
    return UserDto.from(findByUserId(userId));
  }

  private User findByUserId(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new JamGameException(ErrorCode.USER_NOT_FOUND));
  }
}

