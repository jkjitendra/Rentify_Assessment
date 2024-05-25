package com.presidio.rentify.controller;


import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import com.presidio.rentify.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/user/{userId}")
    public ResponseEntity<PropertyResponseDTO> addProperty(@PathVariable Long userId, @Valid @RequestBody PropertyRequestDTO propertyRequestDTO) {
        PropertyResponseDTO property = propertyService.addProperty(userId, propertyRequestDTO);
        return ResponseEntity.ok(property);
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<PropertyResponseDTO>> getPropertiesByOwner(@RequestParam(defaultValue = "0") int pageNo,
                                                                          @RequestParam(defaultValue = "10") int pageSize,
                                                                          @PathVariable Long ownerId) {
        Page<PropertyResponseDTO> properties = propertyService.getPropertiesByOwner(pageNo, pageSize, ownerId);
        return ResponseEntity.ok(properties);
    }

    @GetMapping
    public ResponseEntity<Page<PropertyResponseDTO>> getAllProperties(@RequestParam(defaultValue = "0") int pageNo,
                                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                                      @RequestParam Map<String, String> filters) {
        Page<PropertyResponseDTO> properties = propertyService.getAllProperties(pageNo, pageSize, filters);
        return ResponseEntity.ok(properties);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResponseDTO> updateProperty(@PathVariable Long propertyId, @Valid @RequestBody PropertyRequestDTO propertyRequestDTO) {
        PropertyResponseDTO updatedProperty = propertyService.updateProperty(propertyId, propertyRequestDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long propertyId) {
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{propertyId}/like")
    public ResponseEntity<PropertyResponseDTO> likeProperty(@PathVariable Long propertyId) {
        PropertyResponseDTO likedProperty = propertyService.likeProperty(propertyId);
        return ResponseEntity.ok(likedProperty);
    }
}