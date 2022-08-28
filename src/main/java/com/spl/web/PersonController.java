package com.spl.web;

import com.spl.entity.Car;
import com.spl.entity.Person;
import com.spl.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("persons")
public class PersonController {
    private final PersonService service;

    @GetMapping
    public ResponseEntity<List<Person>> findAllPersons() {
        return new ResponseEntity<>(service.findAll(), OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Person> findPersonById(@PathVariable Long id) {
        return new ResponseEntity<>(service.findById(id), OK);
    }

    @PostMapping
    public ResponseEntity<Person> addPerson(@Valid @RequestBody Person person) {
        return new ResponseEntity<>(service.add(person), CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @Valid @RequestBody Person person) {
        return new ResponseEntity<>(service.update(id, person), OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        service.remove(id);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{id}/cars")
    public Set<Car> getPersonsCars(@PathVariable Long id) {
        Person person = service.findById(id);
        return person.getCars();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Set<String> handleValidationException(MethodArgumentNotValidException ex) {
        Set<String> errors = new HashSet<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return errors;
    }
}
