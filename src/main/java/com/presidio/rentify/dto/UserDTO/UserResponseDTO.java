package com.presidio.rentify.dto.UserDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String role;  // Role can be "SELLER" or "BUYER"
}