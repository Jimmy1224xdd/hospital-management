package com.hospital.service;

import com.hospital.dto.PacienteDTO;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Paciente;
import com.hospital.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de paciente inválido");
        }
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
    }

    public Paciente crear(PacienteDTO dto) {
        Paciente paciente = toEntity(dto);
        // BUG INTENCIONAL: No valida si el email ya existe antes de guardar
        return pacienteRepository.save(paciente);
    }

    public Paciente actualizar(Long id, PacienteDTO dto) {
        Paciente paciente = buscarPorId(id);
        if (dto.getNombre() != null) paciente.setNombre(dto.getNombre());
        if (dto.getApellido() != null) paciente.setApellido(dto.getApellido());
        if (dto.getFechaNacimiento() != null) paciente.setFechaNacimiento(dto.getFechaNacimiento());
        if (dto.getEmail() != null) paciente.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) paciente.setTelefono(dto.getTelefono());
        if (dto.getDireccion() != null) paciente.setDireccion(dto.getDireccion());
        if (dto.getActivo() != null) {
            paciente.setActivo(dto.getActivo());
        }
        return pacienteRepository.save(paciente);
    }

    public void eliminar(Long id) {
        // BUG INTENCIONAL: Eliminacion fisica sin verificar dependencias (citas, historias)
        // Deberia ser borrado logico o verificar FK constraints
        Paciente paciente = buscarPorId(id);
        pacienteRepository.delete(paciente);
    }

    public List<Paciente> buscarPorNombre(String nombre) {
        // BUG INTENCIONAL: si nombre es null, el metodo falla con NullPointerException
        // Ademas, usa una query nativa que es innecesaria
        return pacienteRepository.buscarPorNombre(nombre);
    }

    public Paciente buscarPorEmail(String email) {
        // BUG INTENCIONAL: no valida si email es null o vacio
        // Tampoco maneja el caso de multiples resultados
        return pacienteRepository.findByEmail(email);
    }

    public double calcularEdadPromedio() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        if (pacientes.isEmpty()) {
            return 0.0;
        }
        long suma = 0;
        for (Paciente p : pacientes) {
            if (p.getFechaNacimiento() != null) {
                suma += java.time.Period.between(p.getFechaNacimiento(), java.time.LocalDate.now()).getYears();
            }
        }
        return (double) suma / pacientes.size();
    }

    private Paciente toEntity(PacienteDTO dto) {
        Paciente p = new Paciente();
        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setEmail(dto.getEmail());
        p.setTelefono(dto.getTelefono());
        p.setDireccion(dto.getDireccion());
        p.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return p;
    }
}
