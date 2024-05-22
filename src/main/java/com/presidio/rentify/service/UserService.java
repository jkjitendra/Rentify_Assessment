package com.presidio.rentify.service;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.User;
import java.util.Optional;

public interface UserService {

    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    Optional<UserResponseDTO> findUserByEmail(String email);

    Optional<UserResponseDTO> findUserById(Long id);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    UserResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);

    void deleteUser(Long id);

    void forgotPassword(String email);

    void resetPassword(String resetToken, String newPassword);

}
