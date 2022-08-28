package com.spl.service;

import com.spl.entity.Car;
import com.spl.repository.CarRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@AllArgsConstructor
public class CarServiceImp implements CarService{

    private final CarRepository repository;

    public List<Car> getCars() {
        return repository.findAll();
    }

    public Car add(Car car) {
        repository.findById(car.getVin()).ifPresent(c -> {
            throw new ResponseStatusException(BAD_REQUEST, "This vin is already exists");
        });
        return repository.save(car);
    }

    public Car findCar(String vin) {
        return repository.findById(vin).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                format("No car with vin %s in DB", vin)));
    }

    @Transactional
    public Car updateCar(String vin, Car car) {
        Car existing = repository.findById(vin).orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                format("No car with vin %s in DB", vin)));
        existing.setVin(car.getVin());
        existing.setNumber(car.getNumber());
        existing.setManufacturer(car.getManufacturer());
        existing.setModel(car.getModel());
        return existing;
    }

    public void remove(String vin) {
        repository.deleteById(vin);
    }
}
