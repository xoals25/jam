package com.tang.game.user.repository;

import com.tang.core.type.SignupPath;
import com.tang.game.user.domain.User;
import com.tang.game.user.type.UserStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailAndSignupPath(String email, SignupPath signupPath);

  Optional<User> findByIdAndStatus(Long userId, UserStatus status);
}
