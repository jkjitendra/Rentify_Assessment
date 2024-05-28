package com.presidio.rentify.controller;


import com.presidio.rentify.constants.AppConstants;
import com.presidio.rentify.dto.APIResponse;
import com.presidio.rentify.dto.PageableResponse;
import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import com.presidio.rentify.security.AuthenticationFacade;
import com.presidio.rentify.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/user/{userId}")
    public ResponseEntity<APIResponse<PropertyResponseDTO>> addProperty(@PathVariable Long userId, @Valid @RequestBody PropertyRequestDTO propertyRequestDTO) {

        String authenticatedUsername = authenticationFacade.getAuthenticatedUsername();
        if (propertyService.isOwner(userId, authenticatedUsername)) {
            PropertyResponseDTO property = propertyService.addProperty(userId, propertyRequestDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "Property added successfully", property));
        }
        return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);

    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<APIResponse<PageableResponse<PropertyResponseDTO>>> getPropertiesByOwner(
                                                                        @RequestParam(value = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                                                        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                        @PathVariable Long ownerId) {

        String authenticatedUsername = authenticationFacade.getAuthenticatedUsername();
        if (propertyService.isOwner(ownerId, authenticatedUsername)) {
            PageableResponse<PropertyResponseDTO> properties = propertyService.getPropertiesByOwner(pageNo, pageSize, ownerId);
            return ResponseEntity.ok(new APIResponse<>(true, "Properties fetched successfully", properties));
        }
        return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);


    }

    @PreAuthorize("hasAuthority('BUYER')")
    @GetMapping
    public ResponseEntity<APIResponse<PageableResponse<PropertyResponseDTO>>> getAllProperties(
                                                                        @RequestParam(value = "pageNo", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNo,
                                                                        @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                        @RequestParam Map<String, String> filters) {

        PageableResponse<PropertyResponseDTO> properties = propertyService.getAllProperties(pageNo, pageSize, filters);
        return ResponseEntity.ok(new APIResponse<>(true, "Properties fetched successfully", properties));

    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PutMapping("/{propertyId}")
    public ResponseEntity<APIResponse<PropertyResponseDTO>> updateProperty(@PathVariable Long propertyId, @Valid @RequestBody PropertyRequestDTO propertyRequestDTO) {

        String authenticatedUsername = authenticationFacade.getAuthenticatedUsername();
        if (propertyService.isOwnerByPropertyId(propertyId, authenticatedUsername)) {
            PropertyResponseDTO updatedProperty = propertyService.updateProperty(propertyId, propertyRequestDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "Property updated successfully", updatedProperty));
        }
        return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);

    }

    @PreAuthorize("hasAuthority('SELLER')")
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<APIResponse<Void>> deleteProperty(@PathVariable Long propertyId) {

        String authenticatedUsername = authenticationFacade.getAuthenticatedUsername();
        if (propertyService.isOwnerByPropertyId(propertyId, authenticatedUsername)) {
            propertyService.deleteProperty(propertyId);
            return ResponseEntity.ok(new APIResponse<>(true, "Property deleted successfully"));
        }
        return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);

    }

    @PreAuthorize("hasAuthority('BUYER')")
    @PostMapping("/{propertyId}/like")
    public ResponseEntity<APIResponse<PropertyResponseDTO>> likeProperty(@PathVariable Long propertyId) {

        String authenticatedUsername = authenticationFacade.getAuthenticatedUsername();
        if (propertyService.isOwnerByPropertyId(propertyId, authenticatedUsername)) {
            PropertyResponseDTO likedProperty = propertyService.likeProperty(propertyId);
            return ResponseEntity.ok(new APIResponse<>(true, "Property liked successfully", likedProperty));
        }
        return new ResponseEntity<>(new APIResponse<>(false, "Access denied"), HttpStatus.FORBIDDEN);
    }
}