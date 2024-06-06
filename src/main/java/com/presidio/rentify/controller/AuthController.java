package com.presidio.rentify.controller;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.AuthDTO.*;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.InvalidTokenException;
import com.presidio.rentify.exception.PasswordNotMatchException;
import com.presidio.rentify.exception.TokenExpiredException;
import com.presidio.rentify.service.AuthService;
import com.presidio.rentify.service.RefreshTokenService;
import com.presidio.rentify.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${app.cookie.secure}")
    private boolean isCookieSecure;

    @Value("${secret.jwt.refresh-expiration-time}")
    private long refreshExpirationTime;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserResponseDTO>> registerUser(@Valid @RequestBody RegisterRequestBody registerRequestBody) {
        UserResponseDTO userResponse = authService.registerUser(registerRequestBody);
        return ResponseEntity.ok(new APIResponse<>(true, "User registered successfully", userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, "Invalid credentials", null, e.getMessage()));
        }
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        AuthResponse authResponse = authService.login(authRequest);

        // Set the refresh token as an HTTP-only cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(refreshExpirationTime/1000) // 7 days
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new APIResponse<>(true, "Login successful", AuthResponse.builder().accessToken(authResponse.getAccessToken()).build()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isCookieSecure) // use this in production
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body("Logged out");
    }

//    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<AuthResponse>> createRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshToken oldRefreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());

        User user = oldRefreshToken.getUser();
        String accessToken = jwtUtil.generateToken(user);

        // Create a new refresh token and delete the old one
        refreshTokenService.deleteRefreshToken(oldRefreshToken.getRefreshToken());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getRefreshToken())
                .build();

        // Set the new refresh token as an HTTP-only cookie
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new APIResponse<>(true, "Token refreshed successfully", AuthResponse.builder().accessToken(authResponse.getAccessToken()).build()));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.debug("Received forgot password request for email: {}", email);
        authService.forgotPassword(email);
        return ResponseEntity.ok(new APIResponse<>(true, "Password reset instructions have been sent to your email: " + email));
    }

    @PostMapping("/verifyOTP/{otp}/{email}")
    public ResponseEntity<APIResponse<String>> verifyOTP(@PathVariable Integer otp, @PathVariable String email) {
        try {
            authService.verifyOTP(otp, email);
            return ResponseEntity.ok(new APIResponse<>(true, "OTP verified!"));
        } catch (InvalidTokenException | TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password/{email}")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO, @PathVariable String email) {
        try {
            authService.resetPassword(resetPasswordDTO, email);
            return ResponseEntity.ok(new APIResponse<>(true, "Password has been reset successfully"));
        } catch (PasswordNotMatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new APIResponse<>(false, "Password do not match", null, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new APIResponse<>(false, "OTP not verified", null, e.getMessage()));
        }
    }

}