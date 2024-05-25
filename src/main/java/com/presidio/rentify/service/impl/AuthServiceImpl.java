package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.PasswordResetToken;
import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.entity.UserRole;
import com.presidio.rentify.exception.InvalidTokenException;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.exception.TokenExpiredException;
import com.presidio.rentify.exception.UserAlreadyExistingException;
import com.presidio.rentify.repository.PasswordResetTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.AuthService;
import com.presidio.rentify.service.RefreshTokenService;
import com.presidio.rentify.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userRequestDTO.getEmail());
        if (userOptional.isEmpty()) {
//            User user = modelMapper.map(userRequestDTO, User.class);
//            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            User user = User.builder()
                    .firstName(userRequestDTO.getFirstName())
                    .lastName(userRequestDTO.getLastName())
                    .email(userRequestDTO.getEmail())
                    .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                    .phoneNumber(userRequestDTO.getPhoneNumber())
                    .role(UserRole.valueOf(userRequestDTO.getRole()))
                    .build();

            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDTO.class);
        } else {
            throw new UserAlreadyExistingException("User", "email", userRequestDTO.getEmail());
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", authRequest.getEmail()));
        final String accessToken = jwtUtil.generateToken(user);
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }


    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        String resetToken = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(resetToken);
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(3600)); // Token expires in 1 hour
        passwordResetTokenRepository.save(token);

        // Send the reset token to the user via email
        String resetUrl = "http://localhost:8080/reset-password?token=" + resetToken;
        String emailText = "To reset your password, click the link below:\n" + resetUrl;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", emailText);

    }

    @Override
    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new InvalidTokenException(resetToken, "Invalid reset token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException(resetToken, token.getExpiryDate().toString());
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token after the password has been reset
        passwordResetTokenRepository.delete(token);
    }

}
