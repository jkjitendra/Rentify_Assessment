package com.presidio.rentify.controller;

import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.AuthDTO.AuthRequest;
import com.presidio.rentify.dto.AuthDTO.AuthResponse;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public APIResponse<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponse = userService.registerUser(userRequestDTO);
        return new APIResponse<>(true, "User registered successfully", userResponse);
    }

    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            return new APIResponse<>(false, "Invalid credentials", null, e.getMessage());
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        return new APIResponse<>(true, "Login successful", new AuthResponse(jwt));
    }
}
