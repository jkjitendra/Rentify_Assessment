package com.presidio.rentify.service.impl;


import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.PropertyLike;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.PropertyLikeRepository;
import com.presidio.rentify.repository.PropertyRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.security.AuthenticationFacade;
import com.presidio.rentify.service.PropertyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyLikeRepository propertyLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public PropertyResponseDTO addProperty(Long userId, PropertyRequestDTO propertyRequestDTO) {
      User owner = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id " + userId));
      if (!"SELLER".equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can add properties");
      }

      Property property = modelMapper.map(propertyRequestDTO, Property.class);
      property.setOwner(owner);

      Property savedProperty = propertyRepository.save(property);
      return modelMapper.map(savedProperty, PropertyResponseDTO.class);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByOwner(Long ownerId, Pageable pageable) {
      User owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", ownerId));
      if (!"SELLER".equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can view their properties");
      }

      Page<Property> properties = propertyRepository.findByOwner(owner, pageable);
      return properties.map(property -> modelMapper.map(property, PropertyResponseDTO.class));
    }

    @Override
    public Page<PropertyResponseDTO> getAllProperties(Pageable pageable, Map<String, String> filters) {
      String place = filters.getOrDefault("place", null);
      Double area = filters.containsKey("area") ? Double.valueOf(filters.get("area")) : null;
      Integer numberOfBedrooms = filters.containsKey("numberOfBedrooms") ? Integer.valueOf(filters.get("numberOfBedrooms")) : null;
      Integer numberOfBathrooms = filters.containsKey("numberOfBathrooms") ? Integer.valueOf(filters.get("numberOfBathrooms")) : null;
      String hospitalsNearby = filters.getOrDefault("hospitalsNearby", null);
      String collegesNearby = filters.getOrDefault("collegesNearby", null);

      Page<Property> properties = propertyRepository.findAllWithFilters(place, area, numberOfBedrooms, numberOfBathrooms, hospitalsNearby, collegesNearby, pageable);

      return properties.map(property -> modelMapper.map(property, PropertyResponseDTO.class));
    }

    @Override
    public PropertyResponseDTO updateProperty(Long propertyId, PropertyRequestDTO propertyRequestDTO) {
      Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
      User owner = property.getOwner();
      if (!"SELLER".equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can update properties");
      }

      modelMapper.map(propertyRequestDTO, property);
      Property updatedProperty = propertyRepository.save(property);
      return modelMapper.map(updatedProperty, PropertyResponseDTO.class);
    }

    @Override
    public void deleteProperty(Long propertyId) {
      Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
      User owner = property.getOwner();
      if (!"SELLER".equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can delete properties");
      }

      propertyRepository.delete(property);
    }

    @Override
    public PropertyResponseDTO likeProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));

        String username = authenticationFacade.getAuthenticatedUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        // Check if the user has already liked the property
        propertyLikeRepository.findByPropertyAndUser(property, user).ifPresent(like -> {
            throw new IllegalStateException("User has already liked this property");
        });

        PropertyLike propertyLike = new PropertyLike();
        propertyLike.setProperty(property);
        propertyLike.setUser(user);
        propertyLikeRepository.save(propertyLike);

        long likeCount = propertyLikeRepository.countByProperty(property);
        PropertyResponseDTO response = modelMapper.map(property, PropertyResponseDTO.class);
        response.setLikes(likeCount);

        // Send update via WebSocket
        messagingTemplate.convertAndSend("/topic/likes", response);

        return response;
    }
}