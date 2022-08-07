package com.spl.service;

import com.spl.entity.Person;
import com.spl.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

import static java.lang.String.*;

@Service
@AllArgsConstructor
public class PersonService {
    private final PersonRepository repository;

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Person findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("No person with id %d in DB", id)));
    }

    public Person add(Person person) {
        return repository.save(person);
    }

    @Transactional
    public Person update(Long id, Person person) {
        Person existing = repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, format("No person with id %d in DB", id)));
        if (person.getFirstName().length() > 0) {//TODO:Move validation elsewhere
            existing.setFirstName(person.getFirstName());
        }
        if (person.getLastName().length() > 0) {
            existing.setLastName(person.getLastName());
        }
        return existing;
    }

    public void remove(Long id) {
        repository.deleteById(id);
    }
}
