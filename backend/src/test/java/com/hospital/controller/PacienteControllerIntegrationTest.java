package com.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.PacienteDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;
import com.hospital.model.Paciente;
import com.hospital.repository.PacienteRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PacienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PacienteRepository pacienteRepository;

    private Paciente testPaciente;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testPaciente = new Paciente("Prueba", "Test", LocalDate.of(1990, 1, 1), "test@test.com", "0999999999", "Direccion");
        testPaciente = pacienteRepository.save(testPaciente);
    }

    @Test
    void testListarTodos() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCrearPaciente() throws Exception {
        PacienteDTO dto = new PacienteDTO();
        dto.setNombre("Integracion");
        dto.setApellido("Test");
        dto.setEmail("int@test.com");
        dto.setTelefono("0999999999");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setActivo(true);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Integracion"))
                .andExpect(jsonPath("$.email").value("int@test.com"));
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        mockMvc.perform(get("/api/pacientes/99999"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testActualizarPaciente() throws Exception {
        PacienteDTO dto = new PacienteDTO();
        dto.setNombre("NuevoNombre");

        mockMvc.perform(put("/api/pacientes/" + testPaciente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("NuevoNombre"));
    }

    @Test
    void testEliminarPaciente() throws Exception {
        mockMvc.perform(delete("/api/pacientes/" + testPaciente.getId()))
                .andExpect(status().isNoContent());
    }
}
