package com.spl.web;

import com.spl.entity.Car;
import com.spl.entity.Person;
import com.spl.service.CarService;
import com.spl.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService service;

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<Car>> findAllCars() {
        return new ResponseEntity<>(service.getCars(), OK);
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@Valid @RequestBody Car car) {
        return new ResponseEntity<>(service.add(car), CREATED);
    }

    @GetMapping("{vin}")
    public ResponseEntity<Car> findCar(@PathVariable String vin) {
       return new ResponseEntity<>(service.findCar(vin), OK);
    }

    @PutMapping("{vin}")
    public ResponseEntity<Car> updateCar(@PathVariable String vin, @Valid @RequestBody Car car) {
        return new ResponseEntity<>(service.updateCar(vin, car), OK);
    }

    @DeleteMapping("{vin}")
    public ResponseEntity<Void> deleteCar(@PathVariable String vin) {
        service.remove(vin);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{vin}/owner")
    public Person getCarsOwner(@PathVariable String vin) {
        Car car = service.findCar(vin);
        return personService.findById(car.getOwner());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
