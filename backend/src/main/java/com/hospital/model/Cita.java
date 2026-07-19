package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Esta si tiene la relacion correcta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(length = 255)
    private String motivo;

    @Column(length = 20)
    private String estado = "PROGRAMADA";

    public Cita() {}

    public Cita(Paciente paciente, Doctor doctor, LocalDateTime fechaHora,
                String motivo, String estado) {
        this.paciente = paciente;
        this.doctor = doctor;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
