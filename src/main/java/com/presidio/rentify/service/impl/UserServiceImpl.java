package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.PasswordNotMatchException;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.*;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private PropertyLikeRepository propertyLikeRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return Optional.ofNullable(modelMapper.map(user, UserResponseDTO.class));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        existingUser.setFirstName(userRequestDTO.getFirstName());
        existingUser.setLastName(userRequestDTO.getLastName());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setPhoneNumber(userRequestDTO.getPhoneNumber());
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    @Override
    @Transactional
    public UserResponseWithTokenDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {

        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), existingUser.getPassword())) {
            throw new PasswordNotMatchException("User", "oldPassword", passwordUpdateDTO.getOldPassword());
        }
        existingUser.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        User updatedUser = userRepository.save(existingUser);

        // Generate new JWT token
        String newToken = jwtUtil.generateToken(updatedUser);

        // Map updated user to UserResponseDTO and create UserResponseWithTokenDTO
        UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
        return new UserResponseWithTokenDTO(userResponseDTO, newToken);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));
        userRepository.delete(user);
    }

}