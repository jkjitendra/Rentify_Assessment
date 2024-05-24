package com.presidio.rentify.repository;

import com.presidio.rentify.entity.Property;
import com.presidio.rentify.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Page<Property> findByOwner(User owner, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE " +
            "(:place IS NULL OR p.place LIKE %:place%) AND " +
            "(:area IS NULL OR p.area = :area) AND " +
            "(:numberOfBedrooms IS NULL OR p.numberOfBedrooms = :numberOfBedrooms) AND " +
            "(:numberOfBathrooms IS NULL OR p.numberOfBathrooms = :numberOfBathrooms) AND " +
            "(:hospitalsNearby IS NULL OR p.hospitalsNearby LIKE %:hospitalsNearby%) AND " +
            "(:collegesNearby IS NULL OR p.collegesNearby LIKE %:collegesNearby%)")
    Page<Property> findAllWithFilters(
            @Param("place") String place,
            @Param("area") Double area,
            @Param("numberOfBedrooms") Integer numberOfBedrooms,
            @Param("numberOfBathrooms") Integer numberOfBathrooms,
            @Param("hospitalsNearby") String hospitalsNearby,
            @Param("collegesNearby") String collegesNearby,
            Pageable pageable);
}