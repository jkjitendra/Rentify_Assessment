package com.presidio.rentify.controller;
import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

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
    public ResponseEntity<APIResponse<UserResponseDTO>> updateUserDetails(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(new APIResponse<>(true, "User updated successfully", updatedUser));
    }

    @PutMapping("/{id}/update-password")
    public ResponseEntity<APIResponse<Void>> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        UserResponseDTO userResponseDTO = userService.updatePassword(id, passwordUpdateDTO);
        return ResponseEntity.ok(new APIResponse<>(true, "Password updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new APIResponse<>(true, "User deleted successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<APIResponse<String>> forgotPassword(@RequestBody String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok(new APIResponse<>(true, "Password reset instructions have been sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<String>> resetPassword(@RequestParam String resetToken, @RequestBody String newPassword) {
        userService.resetPassword(resetToken, newPassword);
        return ResponseEntity.ok(new APIResponse<>(true, "Password has been reset successfully"));
    }
}

