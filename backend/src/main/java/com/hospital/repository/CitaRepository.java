package com.hospital.repository;

import com.hospital.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("SELECT c FROM Cita c JOIN FETCH c.doctor JOIN FETCH c.paciente WHERE c.paciente.id = :pacienteId")
    List<Cita> findByPacienteId(@Param("pacienteId") Long pacienteId);

    @Query("SELECT c FROM Cita c JOIN FETCH c.doctor JOIN FETCH c.paciente WHERE c.doctor.id = :doctorId")
    List<Cita> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT c FROM Cita c JOIN FETCH c.doctor JOIN FETCH c.paciente WHERE c.estado = :estado")
    List<Cita> findByEstado(@Param("estado") String estado);

    @Query("SELECT c FROM Cita c JOIN FETCH c.doctor JOIN FETCH c.paciente WHERE c.fechaHora BETWEEN :inicio AND :fin")
    List<Cita> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Cita c JOIN FETCH c.doctor JOIN FETCH c.paciente WHERE c.estado = :estado ORDER BY c.fechaHora")
    List<Cita> findCitasByEstadoOrdered(@Param("estado") String estado);

    boolean existsByDoctorIdAndFechaHora(Long doctorId, LocalDateTime fechaHora);
}
