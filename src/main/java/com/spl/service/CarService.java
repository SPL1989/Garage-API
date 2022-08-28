package com.spl.service;

import com.spl.entity.Car;

import java.util.List;

public interface CarService {
    List<Car> getCars();

    Car add(Car car);

    Car findCar(String vin);

    Car updateCar(String vin, Car car);

    void remove(String vin);
}
