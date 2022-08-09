package com.spl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


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
    @NotBlank(message = "VIN should not be empty")
    @Length(min = 17, max = 17, message = "VIN should be 17 characters length")
    private String vin;

    @Column(
            name = "number",
            nullable = false
    )
    @NotBlank(message = "Number should not be empty")
    @Size(min = 2, max = 10, message = "Number should be between 2 and 10 characters length")
    private String number;

    @Column(
            name = "manufacturer",
            nullable = false
    )
    @NotBlank(message = "Manufacturer should not be empty")
    @Size(min = 2, max = 20)
    private String manufacturer;

    @Column(
            name = "model",
            nullable = false
    )
    @NotBlank(message = "Model should not be empty")
    @Size(min = 2, max = 20)
    private String model;

    @Column(
            name = "person_id",
            nullable = false
    )
    @Min(value = 1L, message = "Owner Id should be greater then 0")
    private Long owner;
}
