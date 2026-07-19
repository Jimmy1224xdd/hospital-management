package com.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.HistoriaClinicaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;
import com.hospital.model.HistoriaClinica;
import com.hospital.model.Doctor;
import com.hospital.model.Paciente;
import com.hospital.repository.HistoriaClinicaRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PacienteRepository;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HistoriaClinicaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HistoriaClinicaRepository historiaRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    private Paciente testPaciente;
    private Doctor testDoctor;
    private HistoriaClinica testHistoria;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testPaciente = new Paciente("Prueba", "Test", LocalDate.of(1990, 1, 1), "test@test.com", "0999999999", "Dir");
        testPaciente = pacienteRepository.save(testPaciente);
        
        testDoctor = new Doctor("Doc", "Test", "Pediatria", "doc@test.com", "0999", "C1");
        testDoctor = doctorRepository.save(testDoctor);
        
        testHistoria = new HistoriaClinica(testPaciente, testDoctor, "Diagnostico", "Tratamiento", "Obs");
        testHistoria = historiaRepository.save(testHistoria);
    }

    @Test
    void testListarTodas() throws Exception {
        mockMvc.perform(get("/api/historias-clinicas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCrearHistoriaClinica() throws Exception {
        HistoriaClinicaDTO dto = new HistoriaClinicaDTO();
        dto.setPacienteId(testPaciente.getId());
        dto.setDoctorId(testDoctor.getId());
        dto.setDiagnostico("Diagnostico de integracion");
        dto.setTratamiento("Tratamiento de integracion");
        dto.setObservaciones("Observaciones test");

        mockMvc.perform(post("/api/historias-clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diagnostico").value("Diagnostico de integracion"));
    }

    @Test
    void testListarPorPaciente() throws Exception {
        mockMvc.perform(get("/api/historias-clinicas/paciente/" + testPaciente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
