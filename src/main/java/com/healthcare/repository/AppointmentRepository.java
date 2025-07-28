package com.healthcare.repository;

import com.healthcare.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(String status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN ?1 AND ?2")
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = ?1 AND a.appointmentDateTime BETWEEN ?2 AND ?3")
    List<Appointment> findByDoctorIdAndDateRange(Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = ?1 AND a.appointmentDateTime BETWEEN ?2 AND ?3")
    List<Appointment> findByPatientIdAndDateRange(Long patientId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDateTime) = CURRENT_DATE")
    List<Appointment> findTodaysAppointments();
}