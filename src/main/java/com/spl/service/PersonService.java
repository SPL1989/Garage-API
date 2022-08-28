package com.spl.service;

import com.spl.entity.Person;

import java.util.List;

public interface PersonService {
    List<Person> findAll();

    Person findById(Long id);

    Person add(Person person);

    Person update(Long id, Person person);

    void remove(Long id);
}
