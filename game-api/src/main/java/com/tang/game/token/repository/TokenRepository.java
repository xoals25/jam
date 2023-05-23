package com.tang.game.token.repository;

import com.tang.game.token.domain.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByUserId(Long userId);

  Optional<Token> findByJwtRefreshToken(String refreshToken);
}
