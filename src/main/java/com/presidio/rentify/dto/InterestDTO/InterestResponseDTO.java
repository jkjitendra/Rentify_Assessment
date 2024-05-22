package com.presidio.rentify.dto.InterestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestResponseDTO {
  private Long id;
  private Long propertyId;
  private Long buyerId;
  private String buyerEmail;
  private String propertyPlace;
}
