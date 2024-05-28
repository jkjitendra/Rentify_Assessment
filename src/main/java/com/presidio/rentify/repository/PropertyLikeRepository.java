package com.presidio.rentify.repository;

import com.presidio.rentify.entity.PropertyLike;
import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropertyLikeRepository extends JpaRepository<PropertyLike, Long> {

    Optional<PropertyLike> findByPropertyAndUser(Property property, User user);

    long countByProperty(Property property);
}