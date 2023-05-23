package com.tang.game.token.Service;

import com.tang.game.common.exception.JamGameException;
import com.tang.game.common.type.ErrorCode;
import com.tang.game.token.domain.Token;
import com.tang.game.token.repository.TokenRepository;
import com.tang.game.user.domain.User;
import com.tang.game.user.repository.UserRepository;
import com.tang.game.user.type.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenProvider tokenProvider;

  private final TokenRepository tokenRepository;

  private final UserRepository userRepository;

  public String tokenRefresh(String accessToken, String refreshToken) {
    Token token = tokenRepository.findByJwtRefreshToken(refreshToken)
        .orElseThrow(() -> new JamGameException(ErrorCode.NOT_FOUND_REFRESH_TOKEN));

    if (!tokenProvider.validateToken(refreshToken)) {
      throw new JamGameException(ErrorCode.EXPIRE_REFRESH_TOKEN);
    }

    User user = userRepository.findByIdAndStatus(token.getUserId(), UserStatus.VALID)
        .orElseThrow(() -> new JamGameException(ErrorCode.USER_NOT_FOUND));

    String newAccessToken = tokenProvider.newGenerateAccessToken(user);

    token.setJwtAccessToken(newAccessToken);

    tokenRepository.save(token);

    return newAccessToken;
  }
}
