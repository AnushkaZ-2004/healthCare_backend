package com.healthcare.controller;

import com.healthcare.dto.DoctorDto;
import com.healthcare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/active")
    public ResponseEntity<List<DoctorDto>> getActiveDoctors() {
        List<DoctorDto> doctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available")
    public ResponseEntity<List<DoctorDto>> getAvailableDoctors() {
        List<DoctorDto> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<DoctorDto>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<DoctorDto> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        Optional<DoctorDto> doctor = doctorService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctor-id/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorByDoctorId(@PathVariable String doctorId) {
        Optional<DoctorDto> doctor = doctorService.getDoctorByDoctorId(doctorId);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DoctorDto> createDoctor( @RequestBody DoctorDto doctorDto) {
        try {
            DoctorDto createdDoctor = doctorService.createDoctor(doctorDto);
            return ResponseEntity.ok(createdDoctor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id,  @RequestBody DoctorDto doctorDto) {
        try {
            DoctorDto updatedDoctor = doctorService.updateDoctor(id, doctorDto);
            return ResponseEntity.ok(updatedDoctor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok().build();
    }
}