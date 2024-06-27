package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="tbl_address")
public class Address extends AbstractEntity<Integer> {

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Column(name = "floor")
    private String floor;

    @Column(name="building")
    private String building;

    @Column(name="street_number")
    private String streetNumber;

    @Column(name="street")
    private String street;

    @Column(name="city")
    private String city;

    @Column(name="country")
    private String country;

    @Column(name="address_type")
    private Integer addressType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
