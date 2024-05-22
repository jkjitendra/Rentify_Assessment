package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.PasswordResetToken;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.PasswordResetTokenRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

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

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        User user = modelMapper.map(userRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
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

    public UserResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        return userRepository.findById(id).map(user -> {
            if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Old password does not match");
            }
            user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
            User updatedUser = userRepository.save(user);
            return modelMapper.map(updatedUser, UserResponseDTO.class);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "userId", id);
        }
        userRepository.deleteById(id);
    }

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

    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the token after the password has been reset
        passwordResetTokenRepository.delete(token);
    }
}
