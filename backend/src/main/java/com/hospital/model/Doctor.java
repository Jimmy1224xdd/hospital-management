package com.hospital.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctores")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 50)
    private String consultorio;

    public Doctor() {}

    public Doctor(String nombre, String apellido, String especialidad,
                  String email, String telefono, String consultorio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.email = email;
        this.telefono = telefono;
        this.consultorio = consultorio;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getConsultorio() { return consultorio; }
    public void setConsultorio(String consultorio) { this.consultorio = consultorio; }
}
