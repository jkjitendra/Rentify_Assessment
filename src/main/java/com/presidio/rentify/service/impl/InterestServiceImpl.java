package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.InterestDTO.InterestRequestDTO;
import com.presidio.rentify.dto.InterestDTO.InterestResponseDTO;
import com.presidio.rentify.entity.Interest;
import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import com.presidio.rentify.exception.ResourceNotFoundException;
import com.presidio.rentify.repository.InterestRepository;
import com.presidio.rentify.repository.PropertyRepository;
import com.presidio.rentify.repository.UserRepository;
import com.presidio.rentify.security.AuthenticationFacade;
import com.presidio.rentify.service.InterestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InterestServiceImpl implements InterestService {

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public InterestResponseDTO expressInterest(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));

        String username = authenticationFacade.getAuthenticatedUsername();

        // Fetch the buyer (user) entity
        User buyer = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        Interest interest = new Interest();
        interest.setProperty(property);
        interest.setBuyer(buyer);
        interest.setTimestamp(Instant.now());

        Interest savedInterest = interestRepository.save(interest);

        InterestResponseDTO response = modelMapper.map(savedInterest, InterestResponseDTO.class);
        response.setOwnerName(property.getOwner().getFirstName() + " " + property.getOwner().getLastName());
        response.setOwnerEmail(property.getOwner().getEmail());
        response.setOwnerPhone(property.getOwner().getPhoneNumber());
        return response;
    }
}