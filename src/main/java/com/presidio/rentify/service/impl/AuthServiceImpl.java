package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.AuthDTO.RegisterRequestBody;
import com.presidio.rentify.dto.AuthDTO.ResetPasswordDTO;
import com.presidio.rentify.dto.MailBody;
import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.PasswordResetToken;
import com.presidio.rentify.entity.RefreshToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.entity.UserRole;
import com.presidio.rentify.exception.*;
import com.presidio.rentify.repository.PasswordResetTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.AuthService;
import com.presidio.rentify.service.RefreshTokenService;
import com.presidio.rentify.util.JwtUtil;
import com.presidio.rentify.util.OTPGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.presidio.rentify.constants.AppConstants.OTP_SUBJECT;
import static com.presidio.rentify.constants.AppConstants.OTP_TEXT;

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
    public UserResponseDTO registerUser(RegisterRequestBody registerRequestBody) {
        Optional<User> userOptional = userRepository.findByEmail(registerRequestBody.getEmail());
        if (userOptional.isEmpty()) {
//            User user = modelMapper.map(userRequestDTO, User.class);
//            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            User user = User.builder()
                    .firstName(registerRequestBody.getFirstName())
                    .lastName(registerRequestBody.getLastName())
                    .email(registerRequestBody.getEmail())
                    .password(passwordEncoder.encode(registerRequestBody.getPassword()))
                    .phoneNumber(registerRequestBody.getPhoneNumber())
                    .role(UserRole.valueOf(registerRequestBody.getRole()))
                    .build();

            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDTO.class);
        } else {
            throw new UserAlreadyExistingException("User", "email", registerRequestBody.getEmail());
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
        int otp = OTPGenerator.generateOTP();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject(OTP_SUBJECT)
                .text(OTP_TEXT + otp)
                .build();

        PasswordResetToken token = new PasswordResetToken();
        token.setOtp(otp);
        token.setUser(user);
        token.setVerified(false);
        token.setExpirationTime(Instant.now().plusSeconds(300)); // OTP expires in 5 mins
        passwordResetTokenRepository.save(token);

        try {
            emailService.sendEmail(mailBody);
        } catch (MailException e) {
            throw new EmailSendingException(email, "Failed to send email", e);
        }

    }

    @Override
    @Transactional
    public void verifyOTP(Integer otp, String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new InvalidTokenException("otp", "Invalid OTP Received"));

        if (passwordResetToken.getExpirationTime().isBefore(Instant.now())) {
            passwordResetTokenRepository.deleteById(passwordResetToken.getId());
            throw new TokenExpiredException(String.valueOf(otp), passwordResetToken.getExpirationTime().toString());
        }
        passwordResetToken.setVerified(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO, String email) {

        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUserAndVerified(existingUser)
                .orElseThrow(() -> new InvalidTokenException("token", "No verified password reset token found"));


        if (!Objects.equals(resetPasswordDTO.getNewPassword(), resetPasswordDTO.getRepeatPassword())) {
            throw new PasswordNotMatchException("newPassword", resetPasswordDTO.getNewPassword());
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        userRepository.updatePassword(email, encodedPassword);

        // Delete the password reset token after successful password reset
        passwordResetTokenRepository.deleteByUser(existingUser);
    }


}
