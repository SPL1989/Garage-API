package com.spl.web;

import com.spl.entity.Car;
import com.spl.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService service;

    @GetMapping
    public ResponseEntity<List<Car>> findAllCars() {
        return new ResponseEntity<>(service.getCars(), OK);
    }

    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        return new ResponseEntity<>(service.add(car), CREATED);
    }

    @GetMapping("{vin}")
    public ResponseEntity<Car> findCar(@PathVariable String vin) {
       return new ResponseEntity<>(service.findCar(vin), OK);
    }

    @PutMapping("{vin}")
    public ResponseEntity<Car> updateCar(@PathVariable String vin, @RequestBody Car car) {
        return new ResponseEntity<>(service.updateCar(vin, car), OK);
    }

    @DeleteMapping("{vin}")
    public ResponseEntity<Void> deleteCar(@PathVariable String vin) {
        service.remove(vin);
        return new ResponseEntity<>(OK);
    }
}
