package com.spl.web;

import com.spl.entity.Person;
import com.spl.service.PersonService;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PersonController.class)
@WithMockUser(username = "user1")
public class PersonControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonService service;

    Person person = Person.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Cole")
            .cars(new HashSet<>())
            .build();

    String personJson = "{\"id\":1,\"firstName\":\"Alex\",\"lastName\":\"Cole\",\"cars\":[]}";

    @Test
    void findAllPersonsWhenDbIsEmpty() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(content().string("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllPersonsWhenDbIsNotEmpty() throws Exception {
        doReturn(List.of(person)).when(service).findAll();
        mockMvc.perform(get("/persons"))
                .andExpect(content().string("[" + personJson + "]"))
                .andExpect(status().isOk());
    }

    @Test
    void findPersonById() throws Exception {
        doReturn(person).when(service).findById(1L);
        mockMvc.perform(get("/persons/1"))
                .andExpect(content().json(personJson))
                .andExpect(status().isOk());
    }

    @Test
    void findPersonByIdThrowsException() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "No person with id 2 in DB"))
                .when(service).findById(2L);
        mockMvc.perform(get("/persons/2"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"No person with id 2 in DB\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addPersonTest() throws Exception {
        doReturn(person).when(service).add(person);
        mockMvc.perform(post("/persons").contentType(APPLICATION_JSON).content(personJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(content().json(personJson))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePersonTest() throws Exception {
        person.setFirstName("Alexandr");
        personJson = "{\"id\":1,\"firstName\":\"Alexandr\",\"lastName\":\"Cole\",\"cars\":[]}";
        doReturn(person).when(service).update(1L, person);
        mockMvc.perform(put("/persons/1").contentType(APPLICATION_JSON).content(personJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(content().json(personJson))
                .andExpect(status().isOk());
    }

    @Test
    void updatePersonThrowsException() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "No person with id 2 in DB"))
                .when(service).update(2L, person);
        mockMvc.perform(put("/persons/2").contentType(APPLICATION_JSON).content(personJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"No person with id 2 in DB\"",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePersonTest() throws Exception {
        mockMvc.perform(delete("/persons/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        verify(service).remove(1L);
    }

    @Test
    void getPersonsCarsReturnEmptySet() throws Exception {
        doReturn(person).when(service).findById(1L);
        mockMvc.perform(get("/persons/1/cars"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void validationTestWhenPersonIsInValid() throws Exception {
        String inValidPersonJson = "{\"id\":1,\"firstName\":null,\"lastName\":\"Cole\",\"cars\":[]}";
        mockMvc.perform(post("/persons").with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(APPLICATION_JSON).content(inValidPersonJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", Is.is("First name should not be empty")));
    }
}
