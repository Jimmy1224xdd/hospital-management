package com.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.DoctorDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;
import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor testDoctor;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        testDoctor = new Doctor("Doc", "Test", "Pediatria", "doc@test.com", "0999", "C1");
        testDoctor = doctorRepository.save(testDoctor);
    }

    @Test
    void testListarTodos() throws Exception {
        mockMvc.perform(get("/api/doctores"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCrearDoctor() throws Exception {
        DoctorDTO dto = new DoctorDTO();
        dto.setNombre("Dr Integracion");
        dto.setApellido("Test");
        dto.setEspecialidad("Cardiologia");
        dto.setEmail("doc.int@test.com");
        dto.setTelefono("0999999998");
        dto.setConsultorio("101A");

        mockMvc.perform(post("/api/doctores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Dr Integracion"))
                .andExpect(jsonPath("$.especialidad").value("Cardiologia"));
    }

    @Test
    void testBuscarPorEspecialidad() throws Exception {
        mockMvc.perform(get("/api/doctores/buscar-especialidad?q=Cardio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testActualizarDoctor() throws Exception {
        DoctorDTO dto = new DoctorDTO();
        dto.setNombre("Update");
        dto.setApellido("Doctor");
        dto.setEspecialidad("Neurologia");

        mockMvc.perform(put("/api/doctores/" + testDoctor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especialidad").value("Neurologia"));
    }

    @Test
    void testEliminarDoctor() throws Exception {
        mockMvc.perform(delete("/api/doctores/" + testDoctor.getId()))
                .andExpect(status().isNoContent());
    }
}
