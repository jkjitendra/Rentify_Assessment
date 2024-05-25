package com.presidio.rentify.dto.AuthDTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String accessToken;

  private String refreshToken;

}