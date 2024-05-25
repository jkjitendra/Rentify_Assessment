package com.presidio.rentify.service.impl;


import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.exception.TokenExpiredException;
import com.presidio.rentify.repository.RefreshTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${secret.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshExpirationTime))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken rfToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "refreshToken", refreshToken));

        if (rfToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(rfToken);
            throw new TokenExpiredException("refreshToken");
        }

        return rfToken;
    }
}
