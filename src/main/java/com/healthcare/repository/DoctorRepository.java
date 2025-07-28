package com.healthcare.repository;

import com.healthcare.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByDoctorId(String doctorId);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByAvailableTrue();
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByDepartment(String department);

    @Query("SELECT d FROM Doctor d WHERE d.user.active = true")
    List<Doctor> findActiveDoctors();

    @Query("SELECT d FROM Doctor d WHERE d.user.firstName LIKE %?1% OR d.user.lastName LIKE %?1%")
    List<Doctor> findByNameContaining(String name);
}