package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.entity.PasswordResetToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.*;
import com.presidio.rentify.repository.PasswordResetTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userRequestDTO.getEmail());
        if (userOptional.isEmpty()) {
            User user = modelMapper.map(userRequestDTO, User.class);
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            User savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDTO.class);
        } else {
            throw new UserAlreadyExistingException("User", "email", userRequestDTO.getEmail());
        }
    }

    @Override
    public Optional<UserResponseDTO> findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return Optional.ofNullable(modelMapper.map(user, UserResponseDTO.class));
    }

    public Optional<UserResponseDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user, UserResponseDTO.class));
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        return userRepository.findById(id).map(user -> {
            modelMapper.map(userRequestDTO, user);
            User updatedUser = userRepository.save(user);
            return modelMapper.map(updatedUser, UserResponseDTO.class);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));
    }

    public UserResponseWithTokenDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        return userRepository.findById(id).map(user -> {
            if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
                throw new PasswordNotMatchException("User", "oldPassword", passwordUpdateDTO.getOldPassword());
            }
            user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
            User updatedUser = userRepository.save(user);

            // Generate new JWT token
            String newToken = jwtUtil.generateToken(updatedUser.getEmail());

            // Map updated user to UserResponseDTO and create UserResponseWithTokenDTO
            UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
            return new UserResponseWithTokenDTO(userResponseDTO, newToken);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));

    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "userId", id);
        }
        userRepository.deleteById(id);
    }

    public void forgotPassword(String email) {
        logger.debug("Attempting to find user with email: {}", email);
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