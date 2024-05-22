package com.presidio.rentify.service.impl;


import com.presidio.rentify.dto.PropertyDTO.PropertyRequestDTO;
import com.presidio.rentify.dto.PropertyDTO.PropertyResponseDTO;
import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.PropertyRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.service.PropertyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ModelMapper modelMapper;

  public PropertyResponseDTO addProperty(Long userId, PropertyRequestDTO propertyRequestDTO) {
    Property property = modelMapper.map(propertyRequestDTO, Property.class);

    User owner = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    property.setOwner(owner);

    Property savedProperty = propertyRepository.save(property);
    return modelMapper.map(savedProperty, PropertyResponseDTO.class);
  }

  public List<PropertyResponseDTO> getPropertiesByOwner(Long ownerId) {
    User owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", ownerId));
    List<Property> properties = propertyRepository.findByOwner(owner);
    return properties.stream().map(property -> modelMapper.map(property, PropertyResponseDTO.class)).collect(Collectors.toList());
  }

  public List<PropertyResponseDTO> getAllProperties() {
    List<Property> properties = propertyRepository.findAll();
    return properties.stream().map(property -> modelMapper.map(property, PropertyResponseDTO.class)).collect(Collectors.toList());
  }

  public PropertyResponseDTO updateProperty(Long propertyId, PropertyRequestDTO propertyRequestDTO) {
    Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
    modelMapper.map(propertyRequestDTO, property);

    Property updatedProperty = propertyRepository.save(property);
    return modelMapper.map(updatedProperty, PropertyResponseDTO.class);
  }

  public void deleteProperty(Long propertyId) {
    Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
    propertyRepository.delete(property);
  }
}

