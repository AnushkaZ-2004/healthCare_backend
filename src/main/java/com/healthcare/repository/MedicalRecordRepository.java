package com.healthcare.repository;

import com.healthcare.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.visitDate BETWEEN ?1 AND ?2")
    List<MedicalRecord> findByVisitDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.id = ?1 ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.diagnosis LIKE %?1%")
    List<MedicalRecord> findByDiagnosisContaining(String diagnosis);
}