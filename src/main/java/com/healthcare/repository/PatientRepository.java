package com.healthcare.repository;

import com.healthcare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientId(String patientId);
    Optional<Patient> findByUserId(Long userId);

    @Query("SELECT p FROM Patient p WHERE p.user.active = true")
    List<Patient> findActivePatients();

    @Query("SELECT p FROM Patient p WHERE p.user.firstName LIKE %?1% OR p.user.lastName LIKE %?1%")
    List<Patient> findByNameContaining(String name);
}