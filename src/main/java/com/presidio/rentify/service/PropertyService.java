package com.presidio.rentify.service;

import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;

import java.util.List;

public interface PropertyService {

  PropertyResponseDTO addProperty(Long userId, PropertyRequestDTO propertyRequestDTO);

  List<PropertyResponseDTO> getPropertiesByOwner(Long ownerId);

  List<PropertyResponseDTO> getAllProperties();

  PropertyResponseDTO updateProperty(Long propertyId, PropertyRequestDTO propertyRequestDTO);

  void deleteProperty(Long propertyId);
}
