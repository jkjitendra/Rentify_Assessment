package com.presidio.rentify.controller;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public APIResponse<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponse = userService.registerUser(userRequestDTO);
        return new APIResponse<>(true, "User registered successfully", userResponse);
    }

    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            logger.info("Received authentication successful {}", authentication.getPrincipal());
        } catch (Exception e) {
            return new APIResponse<>(false, "Invalid credentials", null, e.getMessage());
        }
        logger.info("going to load by username");
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        logger.info("going to generate token {}", userDetails.toString());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        logger.info("generated token being sent  {}", jwt);
        return new APIResponse<>(true, "Login successful", new AuthResponse(jwt));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.debug("Received forgot password request for email: {}", email);
        userService.forgotPassword(email);
        return ResponseEntity.ok(new APIResponse<>(true, "Password reset instructions have been sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestParam String resetToken, @RequestBody String newPassword) {
        userService.resetPassword(resetToken, newPassword);
        return ResponseEntity.ok(new APIResponse<>(true, "Password has been reset successfully"));
    }

}