package com.presidio.rentify.repository;

import com.presidio.rentify.entity.Interest;
import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByProperty(Property property);
    List<Interest> findByBuyer(User buyer);
}
