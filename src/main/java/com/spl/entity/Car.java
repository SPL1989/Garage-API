package com.spl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity(name = "car")
@Table(
        name = "cars",
        uniqueConstraints = {
                @UniqueConstraint(name = "cars_number_UQ", columnNames = "number")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {
    @Id
    @Column(
            name = "vin",
            nullable = false
    )
    private String vin;

    @Column(
            name = "number",
            nullable = false
    )
    private String number;

    @Column(
            name = "manufacturer",
            nullable = false
    )
    private String manufacturer;

    @Column(
            name = "model",
            nullable = false
    )
    private String model;

    @Column(
            name = "person_id",
            nullable = false
    )
    private Long owner;
}
