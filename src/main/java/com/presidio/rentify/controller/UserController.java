package com.presidio.rentify.controller;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.security.AuthenticationFacade;
import com.presidio.rentify.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Optional<UserResponseDTO>>> getUserDetails(@PathVariable Long id) {
        String username = authenticationFacade.getAuthenticatedUsername();
        Optional<UserResponseDTO> user = userService.findUserById(id);
        if (user.isPresent() && user.get().getEmail().equals(username)) {
            return new ResponseEntity<>(new APIResponse<>(true, "User fetched successfully", user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponseDTO>> updateUserDetails(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        String username = authenticationFacade.getAuthenticatedUsername();
        Optional<UserResponseDTO> user = userService.findUserById(id);
        if (user.isPresent() && user.get().getEmail().equals(username)) {
            UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "User updated successfully", updatedUser));
        } else {
            return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/update-password")
    public ResponseEntity<APIResponse<UserResponseWithTokenDTO>> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        String username = authenticationFacade.getAuthenticatedUsername();
        Optional<UserResponseDTO> user = userService.findUserById(id);
        if (user.isPresent() && user.get().getEmail().equals(username)) {
            UserResponseWithTokenDTO userResponseDTO = userService.updatePassword(id, passwordUpdateDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "Password updated successfully", userResponseDTO));
        } else {
            return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long id) {
        String username = authenticationFacade.getAuthenticatedUsername();
        Optional<UserResponseDTO> user = userService.findUserById(id);
        if (user.isPresent() && user.get().getEmail().equals(username)) {
            userService.deleteUser(id);
            return ResponseEntity.ok(new APIResponse<>(true, "User deleted successfully"));
        } else {
            return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);
        }
    }

}