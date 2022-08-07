package com.spl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    private String firstName;

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
