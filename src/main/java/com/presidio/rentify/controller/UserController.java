package com.presidio.rentify.controller;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Optional<UserResponseDTO>>> getUserDetails(@PathVariable Long id) {
        Optional<UserResponseDTO> user = userService.findUserById(id);
        return new ResponseEntity<>(new APIResponse<>(true, "User fetched successfully", user), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponseDTO>> updateUserDetails(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(new APIResponse<>(true, "User updated successfully", updatedUser));
    }

    @PutMapping("/{id}/update-password")
    public ResponseEntity<APIResponse<UserResponseWithTokenDTO>> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        UserResponseWithTokenDTO userResponseDTO = userService.updatePassword(id, passwordUpdateDTO);
        return ResponseEntity.ok(new APIResponse<>(true, "Password updated successfully", userResponseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new APIResponse<>(true, "User deleted successfully"));
    }

}