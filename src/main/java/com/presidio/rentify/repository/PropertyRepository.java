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
            "(:price IS NULL OR p.price = :price) AND " +
            "(:numberOfBedrooms IS NULL OR p.numberOfBedrooms = :numberOfBedrooms) AND " +
            "(:numberOfBathrooms IS NULL OR p.numberOfBathrooms = :numberOfBathrooms) AND " +
            "(:numberOfHospitalsNearby IS NULL OR p.numberOfHospitalsNearby = :numberOfHospitalsNearby) AND " +
            "(:numberOfSchoolsNearby IS NULL OR p.numberOfSchoolsNearby = :numberOfSchoolsNearby) AND " +
            "(:numberOfCollegesNearby IS NULL OR p.numberOfCollegesNearby = :numberOfCollegesNearby) AND " +
            "(:numberOfShoppingMallsNearby IS NULL OR p.numberOfShoppingMallsNearby = :numberOfShoppingMallsNearby) AND " +
            "(:numberOfPublicTransportsNearby IS NULL OR p.numberOfPublicTransportsNearby = :numberOfPublicTransportsNearby)")
    Page<Property> findAllWithFilters(
            @Param("place") String place,
            @Param("area") Double area,
            @Param("price") Double price,
            @Param("numberOfBedrooms") Integer numberOfBedrooms,
            @Param("numberOfBathrooms") Integer numberOfBathrooms,
            @Param("numberOfHospitalsNearby") Integer numberOfHospitalsNearby,
            @Param("numberOfSchoolsNearby") Integer numberOfSchoolsNearby,
            @Param("numberOfCollegesNearby") Integer numberOfCollegesNearby,
            @Param("numberOfShoppingMallsNearby") Integer numberOfShoppingMallsNearby,
            @Param("numberOfPublicTransportsNearby") Integer numberOfPublicTransportsNearby,
            Pageable pageable);
}