package com.presidio.rentify.dto.PropertyDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyResponseDTO {

    private Long id;

    private String place;

    private double area;

    private int numberOfBedrooms;

    private int numberOfBathrooms;

    private int numberOfHospitalsNearby;

    private int numberOfCollegesNearby;

    private int numberOfSchoolsNearby;

    private int numberOfShoppingMallsNearby;

    private int numberOfPublicTransportsNearby;

    private double price;

    private String description;

    private Long ownerId;

    private long likes;

}