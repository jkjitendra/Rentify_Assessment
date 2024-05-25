package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.UserDTO.PasswordUpdateDTO;
import com.presidio.rentify.dto.UserDTO.UserRequestDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseDTO;
import com.presidio.rentify.dto.UserDTO.UserResponseWithTokenDTO;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.PasswordNotMatchException;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.UserService;
import com.presidio.rentify.util.JwtUtil;
import org.modelmapper.ModelMapper;
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
        return userRepository.findById(id).map(user -> {
            modelMapper.map(userRequestDTO, user);
            User updatedUser = userRepository.save(user);
            return modelMapper.map(updatedUser, UserResponseDTO.class);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));
    }

    @Override
    @Transactional
    public UserResponseWithTokenDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        return userRepository.findById(id).map(user -> {
            if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
                throw new PasswordNotMatchException("User", "oldPassword", passwordUpdateDTO.getOldPassword());
            }
            user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
            User updatedUser = userRepository.save(user);

            // Generate new JWT token
            String newToken = jwtUtil.generateToken(updatedUser);

            // Map updated user to UserResponseDTO and create UserResponseWithTokenDTO
            UserResponseDTO userResponseDTO = modelMapper.map(updatedUser, UserResponseDTO.class);
            return new UserResponseWithTokenDTO(userResponseDTO, newToken);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "userId", id));

    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "userId", id);
        }
        userRepository.deleteById(id);
    }

}