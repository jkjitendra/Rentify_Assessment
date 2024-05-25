package com.presidio.rentify.service;

import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;

public interface AuthService {

    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    AuthResponse login(AuthRequest authRequest);

    void forgotPassword(String email);

    void resetPassword(String resetToken, String newPassword);
}
