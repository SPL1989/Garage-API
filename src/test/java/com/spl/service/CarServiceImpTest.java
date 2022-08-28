package com.spl.service;

import com.spl.entity.Car;
import com.spl.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CarServiceImpTest {
    @Spy
    @InjectMocks
    CarServiceImp service;

    @Mock
    CarRepository repository;

    Car car = Car.builder()
            .vin("KL1NF193E6K323675")
            .number("AA1111AA")
            .manufacturer("Chevrolet")
            .model("Lacetti")
            .owner(1L)
            .build();

    @Test
    void getCarsReturnEmptyList() {
        assertThat(service.getCars()).isEqualTo(List.of());
    }

    @Test
    void getCarsReturnNotEmptyList() {
        doReturn(List.of(car)).when(repository).findAll();
        assertThat(service.getCars()).isEqualTo(List.of(car));
    }

    @Test
    void addCarTest() {
        doReturn(car).when(repository).save(car);
        assertThat(service.add(car)).isEqualTo(car);
    }

    @Test
    void addCarThrowsException() {
        doReturn(Optional.of(car)).when(repository).findById(car.getVin());
        assertThrows(ResponseStatusException.class, () -> service.add(car));
    }

    @Test
    void findCarTest() {
        doReturn(Optional.of(car)).when(repository).findById("KL1NF193E6K323675");
        assertThat(service.findCar("KL1NF193E6K323675")).isEqualTo(car);
    }

    @Test
    void findCarThrowsException() {
        assertThrows(ResponseStatusException.class, () -> service.findCar("KL1NF193E6K323675"));
    }

    @Test
    void updateCarTest() {
        doReturn(Optional.of(car)).when(repository).findById("KL1NF193E6K323675");
        car.setNumber("XXXXX");
        assertThat(service.updateCar("KL1NF193E6K323675", car)).isEqualTo(car);
    }

    @Test
    void updateCarThrowsException() {
        assertThrows(ResponseStatusException.class, () -> service.updateCar("KL1NF193E6K323675", car));
    }

    @Test
    void removeCarTest() {
        service.remove("KL1NF193E6K323675");
        verify(repository).deleteById("KL1NF193E6K323675");
    }
}
