package com.presidio.rentify.dto.InterestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestRequestDTO {

    private Long id;

    @NotBlank
    private Long propertyId;

    @NotBlank
    private Long buyerId;
}