package com.presidio.rentify.service;

import com.presidio.rentify.dto.InterestDTO.InterestRequestDTO;
import com.presidio.rentify.dto.InterestDTO.InterestResponseDTO;

import java.util.List;

public interface InterestService {

  InterestResponseDTO addInterest(InterestRequestDTO interestRequestDTO);

  List<InterestResponseDTO> getInterestsByProperty(Long propertyId);

  List<InterestResponseDTO> getInterestsByBuyer(Long buyerId);

  void deleteInterest(Long interestId);
}
