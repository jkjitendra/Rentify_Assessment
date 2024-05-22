package com.presidio.rentify.dto.PropertyDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyRequestDTO {
  private String place;
  private double area;
  private int numberOfBedrooms;
  private int numberOfBathrooms;
  private String hospitalsNearby;
  private String collegesNearby;
}

