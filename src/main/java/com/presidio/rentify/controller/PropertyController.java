package com.presidio.rentify.controller;


import com.presidio.rentify.constants.AppConstants;
import com.presidio.rentify.dto.PageableResponse;
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
    public ResponseEntity<PageableResponse<PropertyResponseDTO>> getPropertiesByOwner(
                                                                        @RequestParam(value = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                                                        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                        @PathVariable Long ownerId) {
        PageableResponse<PropertyResponseDTO> properties = propertyService.getPropertiesByOwner(pageNo, pageSize, ownerId);
        return ResponseEntity.ok(properties);
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping
    public ResponseEntity<PageableResponse<PropertyResponseDTO>> getAllProperties(
                                                                        @RequestParam(value = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                                                        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                        @RequestParam Map<String, String> filters) {
        PageableResponse<PropertyResponseDTO> properties = propertyService.getAllProperties(pageNo, pageSize, filters);
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