package com.spl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "person")
@Table(name = "persons")
public class Person {
    @Id
    @SequenceGenerator(
            name = "persons_generator",
            sequenceName = "persons_generator",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "persons_generator"
    )
    @Column(
            updatable = false
    )
    Long id;

    @NotBlank(message = "First name should not be empty")
    @Size(min = 2, max = 20, message = "First name should be between 2 and 20 characters length")
    private String firstName;

    @NotBlank(message = "Last name should not be empty")
    @Size(min = 2, max = 20, message = "Last name should be between 2 and 20 characters length")
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "person_id")
    private Set<Car> cars = new HashSet<>();

    public Person(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

}
