package com.presidio.rentify.service;

import com.presidio.rentify.dto.PageableResponse;
import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PropertyService {

    PropertyResponseDTO addProperty(Long userId, PropertyRequestDTO propertyRequestDTO);

    PageableResponse<PropertyResponseDTO> getPropertiesByOwner(Integer pageNo, Integer pageSize, Long ownerId);

    PageableResponse<PropertyResponseDTO> getAllProperties(Integer pageNo, Integer pageSize, Map<String, String> filters);

    PropertyResponseDTO updateProperty(Long propertyId, PropertyRequestDTO propertyRequestDTO);

    void deleteProperty(Long propertyId);

    PropertyResponseDTO likeProperty(Long propertyId);

    boolean isOwner(Long userId, String authenticatedUsername);

    boolean isOwnerByPropertyId(Long propertyId, String authenticatedUsername);
}