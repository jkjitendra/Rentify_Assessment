package com.presidio.rentify.dto.PropertyDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private double area;

    @NotNull
    private int numberOfBedrooms;

    @NotNull
    private int numberOfBathrooms;

    @NotNull
    private double price;

    @NotBlank
    private String description;

    private int numberOfHospitalsNearby;

    private int numberOfSchoolsNearby;

    private int numberOfCollegesNearby;

    private int numberOfShoppingMallsNearby;

    private int numberOfPublicTransportsNearby;

}