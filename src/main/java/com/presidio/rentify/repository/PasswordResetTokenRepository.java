package com.presidio.rentify.repository;

import com.presidio.rentify.entity.PasswordResetToken;
import com.presidio.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByToken(String token);
  Optional<PasswordResetToken> findByUser(User user);
}
