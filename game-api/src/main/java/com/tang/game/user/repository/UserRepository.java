package com.tang.game.user.repository;

import com.tang.core.type.SignupPath;
import com.tang.game.user.domain.User;
import com.tang.game.user.type.UserStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmailAndSignupPathAndStatus(String email, SignupPath signupPath, UserStatus status);

  Optional<User> findByIdAndStatus(Long userId, UserStatus status);

  boolean existsByEmailAndStatus(String email, UserStatus status);

  boolean existsByEmailAndDeletedAtNullOrDeletedAtBefore(String email, LocalDateTime localDateTime);
}
