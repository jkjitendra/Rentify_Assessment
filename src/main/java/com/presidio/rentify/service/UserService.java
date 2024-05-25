package com.presidio.rentify.service;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.entity.User;
import java.util.Optional;

public interface UserService {

    Optional<UserResponseDTO> findUserById(Long id);

    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);

    UserResponseWithTokenDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);

    void deleteUser(Long id);

}