package com.presidio.rentify.controller;


import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import com.presidio.rentify.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

  @Autowired
  private PropertyService propertyService;

  @PostMapping("/user/{userId}")
  public ResponseEntity<PropertyResponseDTO> addProperty(@PathVariable Long userId, @RequestBody PropertyRequestDTO propertyRequestDTO) {
    PropertyResponseDTO property = propertyService.addProperty(userId, propertyRequestDTO);
    return ResponseEntity.ok(property);
  }

  @GetMapping("/owner/{ownerId}")
  public ResponseEntity<List<PropertyResponseDTO>> getPropertiesByOwner(@PathVariable Long ownerId) {
    List<PropertyResponseDTO> properties = propertyService.getPropertiesByOwner(ownerId);
    return ResponseEntity.ok(properties);
  }

  @GetMapping
  public ResponseEntity<List<PropertyResponseDTO>> getAllProperties() {
    List<PropertyResponseDTO> properties = propertyService.getAllProperties();
    return ResponseEntity.ok(properties);
  }

  @PutMapping("/{propertyId}")
  public ResponseEntity<PropertyResponseDTO> updateProperty(@PathVariable Long propertyId, @RequestBody PropertyRequestDTO propertyRequestDTO) {
    PropertyResponseDTO updatedProperty = propertyService.updateProperty(propertyId, propertyRequestDTO);
    return ResponseEntity.ok(updatedProperty);
  }

  @DeleteMapping("/{propertyId}")
  public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
    propertyService.deleteProperty(propertyId);
    return ResponseEntity.noContent().build();
  }
}

