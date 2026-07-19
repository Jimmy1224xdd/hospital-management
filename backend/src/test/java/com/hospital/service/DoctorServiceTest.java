package com.hospital.service;

import com.hospital.dto.DoctorDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor1;
    private DoctorDTO doctorDTO;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor("Gregory", "House", "Diagnostico", "house@test.com", "555-0100", "201");
        doctor1.setId(1L);

        doctorDTO = new DoctorDTO();
        doctorDTO.setNombre("Allison");
        doctorDTO.setApellido("Cameron");
        doctorDTO.setEspecialidad("Inmunologia");
    }

    // Casos Felices
    @Test
    void testListarTodos() {
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(doctor1));
        List<Doctor> doctores = doctorService.listarTodos();
        assertFalse(doctores.isEmpty());
        assertEquals(1, doctores.size());
    }

    @Test
    void testBuscarPorId_Exitoso() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        Doctor encontrado = doctorService.buscarPorId(1L);
        assertNotNull(encontrado);
        assertEquals("Gregory", encontrado.getNombre());
    }

    @Test
    void testCrear_Exitoso() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor1);
        Doctor creado = doctorService.crear(doctorDTO);
        assertNotNull(creado);
    }

    @Test
    void testActualizar_Exitoso() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor1);
        Doctor actualizado = doctorService.actualizar(1L, doctorDTO);
        assertEquals("Allison", actualizado.getNombre()); // asume que se actualizó el objeto
    }

    // Casos Límite
    @Test
    void testBuscarPorNombreCompleto_SinResultados() {
        when(doctorRepository.findByNombreAndApellido("Desconocido", "Persona")).thenReturn(Collections.emptyList());
        List<Doctor> doctores = doctorService.buscarPorNombreCompleto("Desconocido", "Persona");
        assertTrue(doctores.isEmpty());
    }

    @Test
    void testBuscarPorEspecialidad() {
        when(doctorRepository.findByEspecialidadContainingIgnoreCase("Diag")).thenReturn(Arrays.asList(doctor1));
        List<Doctor> doctores = doctorService.buscarPorEspecialidad("Diag");
        assertFalse(doctores.isEmpty());
        assertEquals(1, doctores.size());
    }

    // Manejo de Errores
    @Test
    void testBuscarPorId_NoEncontrado() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.buscarPorId(99L));
    }

    @Test
    void testEliminar_Exitoso() {
        doNothing().when(doctorRepository).deleteById(1L);
        doctorService.eliminar(1L);
        verify(doctorRepository, times(1)).deleteById(1L);
    }
}
