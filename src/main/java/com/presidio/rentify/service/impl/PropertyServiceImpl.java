package com.presidio.rentify.service.impl;


import com.presidio.rentify.dto.PageableResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.presidio.rentify.entity.UserRole.SELLER;

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
    @Transactional
    public PropertyResponseDTO addProperty(Long userId, PropertyRequestDTO propertyRequestDTO) {
      User owner = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id " + userId));
      if (!SELLER.equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can add properties");
      }

      Property property = modelMapper.map(propertyRequestDTO, Property.class);
      property.setOwner(owner);

      Property savedProperty = propertyRepository.save(property);
      return modelMapper.map(savedProperty, PropertyResponseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<PropertyResponseDTO> getPropertiesByOwner(Integer pageNo, Integer pageSize, Long ownerId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", ownerId));
        if (!SELLER.equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can view their properties");
        }

        Page<Property> properties = propertyRepository.findByOwner(owner, pageable);
        List<PropertyResponseDTO> propertyResponseDTOList = properties.stream()
                .map(property -> modelMapper.map(property, PropertyResponseDTO.class))
                .collect(Collectors.toList());

        PageableResponse<PropertyResponseDTO> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(propertyResponseDTOList);
        pageableResponse.setPageNumber(properties.getNumber());
        pageableResponse.setSize(properties.getSize());
        pageableResponse.setTotalElements(properties.getTotalElements());
        pageableResponse.setTotalPages(properties.getTotalPages());
        pageableResponse.setLastPage(properties.isLast());
        return pageableResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<PropertyResponseDTO> getAllProperties(Integer pageNo, Integer pageSize, Map<String, String> filters) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        String place = filters.getOrDefault("place", null);
        Double area = filters.containsKey("area") ? Double.valueOf(filters.get("area")) : null;
        Double price = filters.containsKey("price") ? Double.valueOf(filters.get("price")) : null;
        Integer numberOfBedrooms = filters.containsKey("numberOfBedrooms") ? Integer.valueOf(filters.get("numberOfBedrooms")) : null;
        Integer numberOfBathrooms = filters.containsKey("numberOfBathrooms") ? Integer.valueOf(filters.get("numberOfBathrooms")) : null;
//        Integer numberOfHospitalsNearby = filters.getOrDefault("numberOfHospitalsNearby", null);
        Integer numberOfHospitalsNearby = filters.containsKey("numberOfHospitalsNearby") ? Integer.valueOf(filters.get("numberOfHospitalsNearby")) : null;
        Integer numberOfSchoolsNearBy = filters.containsKey("numberOfSchoolsNearBy") ? Integer.valueOf(filters.get("numberOfSchoolsNearBy")) : null;
        Integer numberOfCollegesNearby = filters.containsKey("numberOfCollegesNearby") ? Integer.valueOf(filters.get("numberOfCollegesNearby")) : null;
        Integer numberOfShoppingMallsNearby = filters.containsKey("numberOfShoppingMallsNearby") ? Integer.valueOf(filters.get("numberOfShoppingMallsNearby")) : null;
        Integer numberOfPublicTransportsNearby = filters.containsKey("numberOfPublicTransportsNearby") ? Integer.valueOf(filters.get("numberOfPublicTransportsNearby")) : null;

        Page<Property> properties = propertyRepository.findAllWithFilters(place, area, price, numberOfBedrooms, numberOfBathrooms, numberOfHospitalsNearby,
                numberOfSchoolsNearBy, numberOfCollegesNearby, numberOfShoppingMallsNearby, numberOfPublicTransportsNearby, pageable);
        List<PropertyResponseDTO> propertyResponseDTOList = properties.stream()
                .map(property -> modelMapper.map(property, PropertyResponseDTO.class))
                .collect(Collectors.toList());

        PageableResponse<PropertyResponseDTO> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(propertyResponseDTOList);
        pageableResponse.setPageNumber(properties.getNumber());
        pageableResponse.setSize(properties.getSize());
        pageableResponse.setTotalElements(properties.getTotalElements());
        pageableResponse.setTotalPages(properties.getTotalPages());
        pageableResponse.setLastPage(properties.isLast());
        return pageableResponse;
    }

    @Override
    @Transactional
    public PropertyResponseDTO updateProperty(Long propertyId, PropertyRequestDTO propertyRequestDTO) {
      Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
      User owner = property.getOwner();
      if (!SELLER.equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can update properties");
      }

      modelMapper.map(propertyRequestDTO, property);
      Property updatedProperty = propertyRepository.save(property);
      return modelMapper.map(updatedProperty, PropertyResponseDTO.class);
    }

    @Override
    @Transactional
    public void deleteProperty(Long propertyId) {
      Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));
      User owner = property.getOwner();
      if (!SELLER.equals(owner.getRole())) {
        throw new AccessDeniedException("Only sellers can delete properties");
      }

      propertyRepository.delete(property);
    }

    @Override
    @Transactional
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