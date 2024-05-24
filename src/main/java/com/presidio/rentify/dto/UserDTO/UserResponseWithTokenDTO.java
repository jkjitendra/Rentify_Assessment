package com.presidio.rentify.dto.UserDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseWithTokenDTO extends UserResponseDTO {
    private String accessToken;

    public UserResponseWithTokenDTO(UserResponseDTO userResponseDTO, String token) {
        super.setId(userResponseDTO.getId());
        super.setFirstName(userResponseDTO.getFirstName());
        super.setLastName(userResponseDTO.getLastName());
        super.setEmail(userResponseDTO.getEmail());
        super.setPhoneNumber(userResponseDTO.getPhoneNumber());
        super.setRole(userResponseDTO.getRole());
        this.accessToken = token;
    }
}