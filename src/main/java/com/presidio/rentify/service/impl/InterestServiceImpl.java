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
import com.presidio.rentify.service.InterestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

  public InterestResponseDTO addInterest(InterestRequestDTO interestRequestDTO) {
    Property property = propertyRepository.findById(interestRequestDTO.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", interestRequestDTO.getPropertyId()));

    User buyer = userRepository.findById(interestRequestDTO.getBuyerId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", interestRequestDTO.getBuyerId()));

    Interest interest = new Interest();
    interest.setProperty(property);
    interest.setBuyer(buyer);
    interest.setTimestamp(Instant.now());

    Interest savedInterest = interestRepository.save(interest);
    return modelMapper.map(savedInterest, InterestResponseDTO.class);
  }

  public List<InterestResponseDTO> getInterestsByProperty(Long propertyId) {
    Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property", "propertyId", propertyId));

    List<Interest> interests = interestRepository.findByProperty(property);
    return interests.stream().map(interest -> modelMapper.map(interest, InterestResponseDTO.class)).collect(Collectors.toList());
  }

  public List<InterestResponseDTO> getInterestsByBuyer(Long buyerId) {
    User buyer = userRepository.findById(buyerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", buyerId));

    List<Interest> interests = interestRepository.findByBuyer(buyer);
    return interests.stream().map(interest -> modelMapper.map(interest, InterestResponseDTO.class)).collect(Collectors.toList());
  }

  public void deleteInterest(Long interestId) {
    Interest interest = interestRepository.findById(interestId)
            .orElseThrow(() -> new ResourceNotFoundException("Interest", "interestId", interestId));
    interestRepository.delete(interest);
  }
}
