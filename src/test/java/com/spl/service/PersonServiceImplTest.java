package com.spl.service;

import com.spl.entity.Person;
import com.spl.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @Spy
    @InjectMocks
    PersonServiceImpl service;

    @Mock
    PersonRepository repository;

    Person person = Person.builder()
            .id(1L)
            .firstName("Alex")
            .lastName("Cole")
            .cars(new HashSet<>())
            .build();


    @Test
    void findAllReturnEmptyList() {
        assertThat(service.findAll()).isEqualTo(List.of());
    }

    @Test
    void findAllReturnNotEmptyList() {
        doReturn(List.of(person)).when(repository).findAll();
        assertThat(service.findAll()).isEqualTo(List.of(person));
    }

    @Test
    void findByIdTest() {
        doReturn(Optional.ofNullable(person)).when(repository).findById(1L);
        assertThat(service.findById(1L)).isEqualTo(person);
    }

    @Test
    void findByIdThrowsException() {
        assertThrows(ResponseStatusException.class, () -> service.findById(1L));
    }

    @Test
    void addTest() {
        doReturn(person).when(repository).save(person);
        assertThat(service.add(person)).isEqualTo(person);
        verify(repository).save(person);
    }

    @Test
    void updateTest() {
        doReturn(Optional.ofNullable(person)).when(repository).findById(1L);
        assertThat(service.update(1L, person)).isEqualTo(person);
    }

    @Test
    void updatePersonThrowsException() {
        assertThrows(ResponseStatusException.class, () -> service.update(1L, person));
    }

    @Test
    void removeTest() {
        service.remove(1L);
        verify(repository).deleteById(1L);
    }
}