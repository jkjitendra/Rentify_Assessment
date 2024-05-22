package com.presidio.rentify.repository;

import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByOwner(User owner);
}
