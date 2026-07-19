package com.hospital.service;

import com.hospital.dto.PacienteDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Paciente;
import com.hospital.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente1;
    private PacienteDTO pacienteDTO;

    @BeforeEach
    void setUp() {
        paciente1 = new Paciente("Juan", "Perez", LocalDate.of(1990, 1, 1), "juan@test.com", "0999999999", "Quito");
        paciente1.setId(1L);

        pacienteDTO = new PacienteDTO();
        pacienteDTO.setNombre("Carlos");
        pacienteDTO.setApellido("Lopez");
        pacienteDTO.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        pacienteDTO.setEmail("carlos@test.com");
    }

    // Casos Felices
    @Test
    void testListarTodos() {
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(paciente1));
        List<Paciente> pacientes = pacienteService.listarTodos();
        assertFalse(pacientes.isEmpty());
        assertEquals(1, pacientes.size());
    }

    @Test
    void testBuscarPorId_Exitoso() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente1));
        Paciente encontrado = pacienteService.buscarPorId(1L);
        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    void testCrear_Exitoso() {
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente1);
        Paciente creado = pacienteService.crear(pacienteDTO);
        assertNotNull(creado);
        assertEquals("Juan", creado.getNombre()); // mock devuelve paciente1
    }

    @Test
    void testCalcularEdadPromedio_Exitoso() {
        Paciente p2 = new Paciente("Maria", "Gomez", LocalDate.now().minusYears(20), "maria@test.com", "099", "Quito");
        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(paciente1, p2));
        double promedio = pacienteService.calcularEdadPromedio();
        assertTrue(promedio > 0);
    }

    // Casos Límite
    @Test
    void testCalcularEdadPromedio_ListaVacia() {
        when(pacienteRepository.findAll()).thenReturn(Collections.emptyList());
        double promedio = pacienteService.calcularEdadPromedio();
        assertEquals(0.0, promedio);
    }

    @Test
    void testActualizar_Parcial() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente1));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(paciente1);
        PacienteDTO parcial = new PacienteDTO();
        parcial.setNombre("Juanito");
        Paciente actualizado = pacienteService.actualizar(1L, parcial);
        assertEquals("Juanito", actualizado.getNombre());
        assertEquals("Perez", actualizado.getApellido()); // no cambio
    }

    // Manejo de Errores
    @Test
    void testBuscarPorId_NoEncontrado() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> pacienteService.buscarPorId(99L));
    }

    @Test
    void testBuscarPorId_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> pacienteService.buscarPorId(-1L));
        assertThrows(IllegalArgumentException.class, () -> pacienteService.buscarPorId(0L));
        assertThrows(IllegalArgumentException.class, () -> pacienteService.buscarPorId(null));
    }
}
