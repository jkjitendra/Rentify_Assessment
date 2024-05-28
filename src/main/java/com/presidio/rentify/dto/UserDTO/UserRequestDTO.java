package com.presidio.rentify.dto.UserDTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

  @NotEmpty
  @Size(min = 4, message = "Name Should Be Greater Than 4")
  private String firstName;

  @NotEmpty
  @Size(min = 4, message = "Username Should Be Greater Than 4")
  private String lastName;

  @Email(message = "Please Enter Valid Email")
  private String email;

  @NotEmpty(message = "Mobile number must not be empty")
  @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid Mobile Number Format")
  private String phoneNumber;

  @Pattern(regexp = "^(SELLER|BUYER)$", message = "role can be either SELLER, BUYER")
  private String role;  // Role can be "SELLER" or "BUYER"
}