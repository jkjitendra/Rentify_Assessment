package com.presidio.rentify.controller;


import com.presidio.rentify.dto.InterestDTO.InterestRequestDTO;
import com.presidio.rentify.dto.InterestDTO.InterestResponseDTO;
import com.presidio.rentify.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interests")
public class InterestController {

  @Autowired
  private InterestService interestService;

  @PostMapping
  public ResponseEntity<InterestResponseDTO> addInterest(@RequestBody InterestRequestDTO interestRequestDTO) {
    InterestResponseDTO interest = interestService.addInterest(interestRequestDTO);
    return ResponseEntity.ok(interest);
  }

  @GetMapping("/property/{propertyId}")
  public ResponseEntity<List<InterestResponseDTO>> getInterestsByProperty(@PathVariable Long propertyId) {
    List<InterestResponseDTO> interests = interestService.getInterestsByProperty(propertyId);
    return ResponseEntity.ok(interests);
  }

  @GetMapping("/buyer/{buyerId}")
  public ResponseEntity<List<InterestResponseDTO>> getInterestsByBuyer(@PathVariable Long buyerId) {
    List<InterestResponseDTO> interests = interestService.getInterestsByBuyer(buyerId);
    return ResponseEntity.ok(interests);
  }

  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> deleteInterest(@PathVariable Long interestId) {
    interestService.deleteInterest(interestId);
    return ResponseEntity.noContent().build();
  }
}

