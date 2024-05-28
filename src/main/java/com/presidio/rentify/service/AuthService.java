package com.presidio.rentify.service;

import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.AuthDTO.RegisterRequestBody;
import com.presidio.rentify.dto.AuthDTO.ResetPasswordDTO;
import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;

public interface AuthService {

    UserResponseDTO registerUser(RegisterRequestBody registerRequestBody);

    AuthResponse login(AuthRequest authRequest);

    void forgotPassword(String email);

    void verifyOTP(Integer otp, String email);

    void resetPassword(ResetPasswordDTO resetPasswordDTO, String email);
}
