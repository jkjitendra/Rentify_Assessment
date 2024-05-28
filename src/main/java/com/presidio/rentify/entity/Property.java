package com.presidio.rentify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "properties")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String place;

    @Column(nullable = false)
    @NotNull
    private Double area;

    @Column(nullable = false)
    @NotNull
    private Integer numberOfBedrooms;

    @Column(nullable = false)
    @NotNull
    private Integer numberOfBathrooms;

    @Column(nullable = false)
    @NotNull
    private Double price;

    @Column(nullable = false)
    @NotBlank
    private String description;

    private Integer numberOfHospitalsNearby;

    private Integer numberOfSchoolsNearby;

    private Integer numberOfCollegesNearby;

    private Integer numberOfShoppingMallsNearby;

    private Integer numberOfPublicTransportsNearby;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Interest> interests;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<PropertyLike> propertyLikes;

}