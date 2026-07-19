package com.hospital.service;

import com.hospital.dto.HistoriaClinicaDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Doctor;
import com.hospital.model.HistoriaClinica;
import com.hospital.model.Paciente;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.HistoriaClinicaRepository;
import com.hospital.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoriaClinicaServiceTest {

    @Mock
    private HistoriaClinicaRepository historiaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private HistoriaClinicaService historiaService;

    private HistoriaClinica historia;
    private Paciente paciente;
    private Doctor doctor;
    private HistoriaClinicaDTO historiaDTO;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("Juan", "Perez", LocalDate.of(1990, 1, 1), "juan@test.com", "0999999999", "Quito");
        paciente.setId(1L);

        doctor = new Doctor("Gregory", "House", "Diagnostico", "house@test.com", "555-0100", "201");
        doctor.setId(1L);

        historia = new HistoriaClinica(paciente, doctor, "Diagnostico test", "Tratamiento test", "Observacion test");
        historia.setId(1L);

        historiaDTO = new HistoriaClinicaDTO();
        historiaDTO.setPacienteId(1L);
        historiaDTO.setDoctorId(1L);
        historiaDTO.setDiagnostico("Diagnostico test");
    }

    // Casos Felices
    @Test
    void testCrear_Exitosa() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(historiaRepository.save(any(HistoriaClinica.class))).thenReturn(historia);

        HistoriaClinica creada = historiaService.crear(historiaDTO);
        assertNotNull(creada);
    }

    @Test
    void testBuscarPorId_Exitosa() {
        when(historiaRepository.findById(1L)).thenReturn(Optional.of(historia));
        HistoriaClinica encontrada = historiaService.buscarPorId(1L);
        assertNotNull(encontrada);
    }

    @Test
    void testListarPorPaciente() {
        when(historiaRepository.findByPacienteId(1L)).thenReturn(Arrays.asList(historia));
        List<HistoriaClinica> historias = historiaService.listarPorPaciente(1L);
        assertFalse(historias.isEmpty());
    }

    // Casos Límite
    @Test
    void testCrear_SinDoctor() {
        historiaDTO.setDoctorId(null);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(historiaRepository.save(any(HistoriaClinica.class))).thenReturn(historia);

        HistoriaClinica creada = historiaService.crear(historiaDTO);
        assertNotNull(creada);
    }

    // Manejo de Errores
    @Test
    void testCrear_PacienteNoEncontrado() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> historiaService.crear(historiaDTO));
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        when(historiaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> historiaService.buscarPorId(99L));
    }
}
