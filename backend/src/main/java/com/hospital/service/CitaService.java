package com.hospital.service;

import com.hospital.dto.CitaDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Cita;
import com.hospital.model.Doctor;
import com.hospital.model.Paciente;
import com.hospital.repository.CitaRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    public CitaService(CitaRepository citaRepository, DoctorRepository doctorRepository, PacienteRepository pacienteRepository) {
        this.citaRepository = citaRepository;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con ID: " + id));
    }

    public Cita crear(CitaDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor no encontrado"));

        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        if (citaRepository.existsByDoctorIdAndFechaHora(dto.getDoctorId(), dto.getFechaHora())) {
            throw new IllegalArgumentException("El doctor ya tiene una cita en ese horario");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setDoctor(doctor);
        cita.setFechaHora(dto.getFechaHora());
        cita.setMotivo(dto.getMotivo());
        cita.setEstado(dto.getEstado() != null ? dto.getEstado() : "PROGRAMADA");

        return citaRepository.save(cita);
    }

    public Cita actualizar(Long id, CitaDTO dto) {
        Cita cita = buscarPorId(id);
        cita.setFechaHora(dto.getFechaHora());
        cita.setMotivo(dto.getMotivo());
        if (dto.getEstado() != null) {
            cita.setEstado(dto.getEstado());
        }
        return citaRepository.save(cita);
    }

    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }

    public List<Cita> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId);
    }

    public List<Cita> listarPorDoctor(Long doctorId) {
        // BUG INTENCIONAL: N+1 query - accede al doctor para cada cita
        // cuando se serializa a JSON, cada cita carga el doctor por separado
        return citaRepository.findByDoctorId(doctorId);
    }

    public List<Cita> listarPorEstado(String estado) {
        // BUG: N+1 query - el metodo findCitasByEstadoOrdered no usa JOIN FETCH
        return citaRepository.findCitasByEstadoOrdered(estado);
    }

    public List<Cita> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        }
        return citaRepository.findByFechaHoraBetween(inicio, fin);
    }
}
