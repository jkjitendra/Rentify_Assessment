package com.presidio.rentify.service.impl;


import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.exception.TokenExpiredException;
import com.presidio.rentify.repository.RefreshTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.RefreshTokenService;
import com.presidio.rentify.util.GeneratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${secret.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;



    @Override
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Delete existing refresh token if present
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);;

        // Create a new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(GeneratorUtils.generateRefreshToken());
        refreshToken.setExpirationTime(Instant.now().plusMillis(refreshExpirationTime));
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken rfToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "refreshToken", refreshToken));

        if (rfToken.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(rfToken);
            throw new TokenExpiredException("refreshToken");
        }

        return rfToken;
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
