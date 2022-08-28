package com.spl.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spl.entity.Person;
import com.spl.service.PersonServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PersonServiceImpl service;

    ObjectMapper objectMapper = new ObjectMapper();

    Person person = Person.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Cole")
            .cars(new HashSet<>())
            .build();

    String personJson = objectMapper.writeValueAsString(person);

    public PersonControllerTest() throws JsonProcessingException {
    }

    @Test
    @WithMockUser(username = "user1")
    void findAllPersonsWhenDbIsEmpty() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(content().string("[]"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void findAllPersonsWhenDbIsNotEmpty() throws Exception {
        doReturn(List.of(person)).when(service).findAll();
        mockMvc.perform(get("/persons"))
                .andExpect(content().string("[" + personJson + "]"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
    void findPersonById() throws Exception {
        doReturn(person).when(service).findById(1L);
        mockMvc.perform(get("/persons/1"))
                .andExpect(content().json(personJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1")
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
    @WithMockUser(username = "user1")
    void addPersonTest() throws Exception {
        doReturn(person).when(service).add(person);
        mockMvc.perform(post("/persons").contentType(APPLICATION_JSON).content(personJson)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(content().json(personJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user1")
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
    @WithMockUser(username = "user1")
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
    @WithMockUser(username = "user1")
    void deletePersonTest() throws Exception {
        mockMvc.perform(delete("/persons/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        verify(service).remove(1L);
    }

    @Test
    @WithMockUser(username = "user1")
    void getPersonsCarsReturnEmptySet() throws Exception {
        doReturn(person).when(service).findById(1L);
        mockMvc.perform(get("/persons/1/cars"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @WithMockUser(username = "user1")
    void validationTestWhenPersonIsInValid() throws Exception {
        String inValidPersonJson = "{\"id\":1,\"firstName\":null,\"lastName\":\"Cole\",\"cars\":[]}";
        mockMvc.perform(post("/persons").with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(APPLICATION_JSON).content(inValidPersonJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string("[\"First name should not be empty\"]"));
    }

    @Test
    void returnUnauthorizedWhenGetAllPersons() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void returnUnauthorizedWhenGetPerson() throws Exception {
        mockMvc.perform(get("/persons/1"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void returnUnauthorizedWhenGetPersonsCars() throws Exception {
        mockMvc.perform(get("/persons/1/cars"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void returnForbiddenWhenAddPerson() throws Exception {
        mockMvc.perform(post("/persons").contentType(APPLICATION_JSON).content(personJson))
                .andExpect(status().isForbidden());
    }
    @Test
    void returnForbiddenWhenUpdatePerson() throws Exception {
        mockMvc.perform(put("/persons/1").contentType(APPLICATION_JSON).content(personJson))
                .andExpect(status().isForbidden());
    }
    @Test
    void returnForbiddenWhenDeletePerson() throws Exception {
        mockMvc.perform(put("/persons/1"))
                .andExpect(status().isForbidden());
    }
}
