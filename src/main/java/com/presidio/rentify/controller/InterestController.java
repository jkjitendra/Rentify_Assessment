package com.presidio.rentify.controller;


import com.presidio.rentify.dto.InterestDTO.InterestRequestDTO;
import com.presidio.rentify.dto.InterestDTO.InterestResponseDTO;
import com.presidio.rentify.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interests")
public class InterestController {

    @Autowired
    private InterestService interestService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<InterestResponseDTO> expressInterest(@RequestParam Long propertyId) {
        InterestResponseDTO interest = interestService.expressInterest(propertyId);
        return ResponseEntity.ok(interest);
    }
}