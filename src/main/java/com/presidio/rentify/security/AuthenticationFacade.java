package com.presidio.rentify.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Authentication getAuthentication();
    String getAuthenticatedUsername();
}