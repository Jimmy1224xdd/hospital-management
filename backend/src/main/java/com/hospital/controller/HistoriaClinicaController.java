package com.hospital.controller;

import com.hospital.dto.HistoriaClinicaDTO;
import com.hospital.model.HistoriaClinica;
import com.hospital.service.HistoriaClinicaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historias-clinicas")
@CrossOrigin(origins = "http://localhost:3000")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaService;

    public HistoriaClinicaController(HistoriaClinicaService historiaService) {
        this.historiaService = historiaService;
    }

    @GetMapping
    public ResponseEntity<List<HistoriaClinica>> listar() {
        return ResponseEntity.ok(historiaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoriaClinica> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(historiaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<HistoriaClinica> crear(@Valid @RequestBody HistoriaClinicaDTO dto) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(historiaService.crear(dto));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<HistoriaClinica>> listarPorPaciente(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(historiaService.listarPorPaciente(pacienteId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<HistoriaClinica>> listarPorDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(historiaService.listarPorDoctor(doctorId));
    }
}
