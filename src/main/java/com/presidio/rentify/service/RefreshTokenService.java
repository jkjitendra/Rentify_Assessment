package com.presidio.rentify.service;

import com.presidio.rentify.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String email);

    RefreshToken verifyRefreshToken(String refreshToken);
}
