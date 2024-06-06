package com.presidio.rentify.repository;

import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Ref;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUser(User user);

    void deleteByRefreshToken(String refreshToken);
}
