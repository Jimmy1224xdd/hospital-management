package com.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.CitaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;
import com.hospital.model.Cita;
import com.hospital.model.Doctor;
import com.hospital.model.Paciente;
import com.hospital.repository.CitaRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PacienteRepository;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CitaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    private Cita testCita;
    private Paciente testPaciente;
    private Doctor testDoctor;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testPaciente = new Paciente("Prueba", "Test", LocalDate.of(1990, 1, 1), "test@test.com", "0999999999", "Dir");
        testPaciente = pacienteRepository.save(testPaciente);
        
        testDoctor = new Doctor("Doc", "Test", "Pediatria", "doc@test.com", "0999", "C1");
        testDoctor = doctorRepository.save(testDoctor);
        
        testCita = new Cita(testPaciente, testDoctor, LocalDateTime.now().plusDays(1), "Motivo test", "PROGRAMADA");
        testCita = citaRepository.save(testCita);
    }

    @Test
    void testListarTodas() throws Exception {
        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCrearCita() throws Exception {
        CitaDTO dto = new CitaDTO();
        dto.setPacienteId(testPaciente.getId());
        dto.setDoctorId(testDoctor.getId());
        dto.setFechaHora(LocalDateTime.now().plusDays(5));
        dto.setMotivo("Revision de rutina");

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.motivo").value("Revision de rutina"));
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/citas/99999"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testActualizarCita() throws Exception {
        CitaDTO dto = new CitaDTO();
        dto.setPacienteId(testPaciente.getId());
        dto.setDoctorId(testDoctor.getId());
        dto.setFechaHora(LocalDateTime.now().plusDays(5));
        dto.setMotivo("Consulta de seguimiento");

        mockMvc.perform(put("/api/citas/" + testCita.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Consulta de seguimiento"));
    }

    @Test
    void testEliminarCita() throws Exception {
        mockMvc.perform(delete("/api/citas/" + testCita.getId()))
                .andExpect(status().isNoContent());
    }
}
