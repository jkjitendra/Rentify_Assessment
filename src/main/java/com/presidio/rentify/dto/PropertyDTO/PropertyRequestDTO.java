package com.presidio.rentify.dto.PropertyDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyRequestDTO {
  private String place;
  private double area;
  private int numberOfBedrooms;
  private int numberOfBathrooms;
  private String hospitalsNearby;
  private String collegesNearby;
  private String schoolsNearby;
  private String shoppingMallsNearby;
  private String publicTransportNearby;
  private double price;
  private String description;
}