package com.spl.web;

import com.spl.entity.Car;
import com.spl.entity.Person;
import com.spl.service.CarService;
import com.spl.service.PersonService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CarController.class)
@WithMockUser(username = "user1")
public class CarControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CarService service;

    @MockBean
    PersonService personService;

    Car car = Car.builder()
            .vin("KL1NF193E6K323675")
            .number("AA1111AA")
            .manufacturer("Chevrolet")
            .model("Lacetti")
            .owner(1L)
            .build();
    String carJson = "{\"vin\":\"KL1NF193E6K323675\",\"number\":\"AA1111AA\"," +
            "\"manufacturer\":\"Chevrolet\",\"model\":\"Lacetti\",\"owner\":1}";

    Person person = Person.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Cole")
            .cars(Set.of(car))
            .build();

    String personJson = "{\"id\":1,\"firstName\":\"Alex\",\"lastName\":\"Cole\",\"cars\":" +
            "[{\"vin\":\"KL1NF193E6K323675\",\"number\":\"AA1111AA\",\"manufacturer\":\"Chevrolet\",\"model\":\"Lacetti\",\"owner\":1}]}";

    @Test
    void findAllCarsReturnEmptyList() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(content().string("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllReturnListOfCars() throws Exception {
        doReturn(List.of(car)).when(service).getCars();
        mockMvc.perform(get("/cars"))
                .andExpect(content().string("[" + carJson + "]"))
                .andExpect(status().isOk());
    }

    @Test
    void addCarTest() throws Exception {
        doReturn(car).when(service).add(car);
        mockMvc.perform(post("/cars").contentType(APPLICATION_JSON).content(carJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(content().string(carJson))
                .andExpect(status().isCreated());
    }

    @Test
    void addCarThrowsException() throws Exception {
        doThrow(new ResponseStatusException(BAD_REQUEST, "This vin is already exists"))
                .when(service).add(car);
        mockMvc.perform(post("/cars").contentType(APPLICATION_JSON).content(carJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"This vin is already exists\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findCarTest() throws Exception {
        doReturn(car).when(service).findCar("KL1NF193E6K323675");
        mockMvc.perform(get("/cars/KL1NF193E6K323675"))
                .andExpect(content().string(carJson))
                .andExpect(status().isOk());
    }

    @Test
    void findCarThrowsException() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "No car with vin WF0AXXWPDA3U77669 in DB"))
                .when(service).findCar("WF0AXXWPDA3U77669");
        mockMvc.perform(get("/cars/WF0AXXWPDA3U77669"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"No car with vin WF0AXXWPDA3U77669 in DB\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCarTest() throws Exception {
        car.setNumber("XXXXX");
        String carJson = "{\"vin\":\"KL1NF193E6K323675\",\"number\":\"XXXXX\",\"manufacturer\":\"Chevrolet\",\"model\":\"Lacetti\",\"owner\":1}";
        when(service.updateCar("KL1NF193E6K323675", car)).thenReturn(car);
        mockMvc.perform(put("/cars/KL1NF193E6K323675").contentType(APPLICATION_JSON).content(carJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(content().json(carJson))
                .andExpect(status().isOk());
    }

    @Test
    void updateCarThrowsException() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "No car with vin KL1NF193E6K323675 in DB"))
                .when(service).updateCar("KL1NF193E6K323675", car);
        mockMvc.perform(put("/cars/KL1NF193E6K323675").contentType(APPLICATION_JSON).content(carJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"No car with vin KL1NF193E6K323675 in DB\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCarTest() throws Exception {
        mockMvc.perform(delete("/cars/KL1NF193E6K323675")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        verify(service).remove("KL1NF193E6K323675");
    }

    @Test
    void getCarsOwnerTest() throws Exception {
        doReturn(person).when(personService).findById(1L);
        doReturn(car).when(service).findCar("KL1NF193E6K323675");
        mockMvc.perform(get("/cars/KL1NF193E6K323675/owner"))
                .andExpect(status().isOk())
                .andExpect(content().string(personJson));
    }

    @Test
    void validationTestWhenCarIsInValid() throws Exception {
        String inValidCarJson = "{\"vin\":\"KL1NF193E6\",\"number\":\"AA1111AA\"," +
                "\"manufacturer\":\"Chevrolet\",\"model\":\"Lacetti\",\"owner\":1}";
        mockMvc.perform(post("/cars").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(inValidCarJson).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vin", Is.is("VIN should be 17 characters length")))
                .andExpect(content().contentType(APPLICATION_JSON));
    }

}
