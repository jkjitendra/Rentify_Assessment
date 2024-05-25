package com.presidio.rentify.dto.PropertyDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequestDTO {

    @NotBlank
    private String place;

    @NotBlank
    private double area;

    @NotBlank
    private int numberOfBedrooms;

    @NotBlank
    private int numberOfBathrooms;

    @NotBlank
    private double price;

    @NotBlank
    private String description;

    private int numberOfHospitalsNearby;

    private int numberOfSchoolsNearby;

    private int numberOfCollegesNearby;

    private int numberOfShoppingMallsNearby;

    private int numberOfPublicTransportNearby;

}