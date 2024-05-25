package com.presidio.rentify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private Double area;

    @Column(nullable = false)
    @NotBlank
    private Integer numberOfBedrooms;

    @Column(nullable = false)
    @NotBlank
    private Integer numberOfBathrooms;

    @Column(nullable = false)
    @NotBlank
    private Double price;

    @Column(nullable = false)
    @NotBlank
    private String description;

    private Integer numberOfHospitalsNearby;

    private Integer numberOfSchoolsNearby;

    private Integer numberOfCollegesNearby;

    private Integer numberOfShoppingMallsNearby;

    private Integer numberOfPublicTransportsNearby;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Interest> interests;

}