package com.hospital.service;

import com.hospital.dto.CitaDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Cita;
import com.hospital.model.Doctor;
import com.hospital.model.Paciente;
import com.hospital.repository.CitaRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private CitaService citaService;

    private Cita cita;
    private Doctor doctor;
    private Paciente paciente;
    private CitaDTO citaDTO;

    @BeforeEach
    void setUp() {
        doctor = new Doctor("Gregory", "House", "Diagnostico", "house@test.com", "555-0100", "201");
        doctor.setId(1L);
        
        paciente = new Paciente("Juan", "Perez", LocalDate.of(1990, 1, 1), "juan@test.com", "0999999999", "Quito");
        paciente.setId(1L);

        cita = new Cita(paciente, doctor, LocalDateTime.now().plusDays(1), "Consulta general", "PROGRAMADA");
        cita.setId(1L);

        citaDTO = new CitaDTO();
        citaDTO.setDoctorId(1L);
        citaDTO.setPacienteId(1L);
        citaDTO.setFechaHora(LocalDateTime.now().plusDays(1));
        citaDTO.setMotivo("Dolor de cabeza");
    }

    // Casos Felices
    @Test
    void testCrearCita_Exitosa() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(citaRepository.existsByDoctorIdAndFechaHora(any(), any())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita creada = citaService.crear(citaDTO);
        assertNotNull(creada);
    }

    @Test
    void testBuscarPorId_Exitosa() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        Cita encontrada = citaService.buscarPorId(1L);
        assertNotNull(encontrada);
    }

    @Test
    void testListarPorRangoFechas() {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = LocalDateTime.now().plusDays(2);
        when(citaRepository.findByFechaHoraBetween(inicio, fin)).thenReturn(Arrays.asList(cita));
        List<Cita> citas = citaService.listarPorRangoFechas(inicio, fin);
        assertFalse(citas.isEmpty());
    }

    // Casos Límite
    @Test
    void testCrearCita_DobleBooking() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(citaRepository.existsByDoctorIdAndFechaHora(any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> citaService.crear(citaDTO));
    }
    
    @Test
    void testListarPorRangoFechas_FechasInvalidas() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(2);
        LocalDateTime fin = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> citaService.listarPorRangoFechas(inicio, fin));
    }

    // Manejo de Errores
    @Test
    void testCrearCita_DoctorNoEncontrado() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.crear(citaDTO));
    }

    @Test
    void testCrearCita_PacienteNoEncontrado() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> citaService.crear(citaDTO));
    }
}
