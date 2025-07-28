package com.healthcare.controller;

import com.healthcare.dto.MedicalRecordDto;
import com.healthcare.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:3000")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<List<MedicalRecordDto>> getAllMedicalRecords() {
        List<MedicalRecordDto> records = medicalRecordService.getAllMedicalRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> getMedicalRecordById(@PathVariable Long id) {
        Optional<MedicalRecordDto> record = medicalRecordService.getMedicalRecordById(id);
        return record.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordDto>> getMedicalRecordsByPatient(@PathVariable Long patientId) {
        List<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByPatient(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordDto>> getMedicalRecordsByDoctor(@PathVariable Long doctorId) {
        List<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByDoctor(doctorId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDto> getMedicalRecordByAppointment(@PathVariable Long appointmentId) {
        Optional<MedicalRecordDto> record = medicalRecordService.getMedicalRecordByAppointment(appointmentId);
        return record.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<MedicalRecordDto>> getMedicalRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MedicalRecordDto> records = medicalRecordService.getMedicalRecordsByDateRange(startDate, endDate);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalRecordDto>> searchByDiagnosis(@RequestParam String diagnosis) {
        List<MedicalRecordDto> records = medicalRecordService.searchByDiagnosis(diagnosis);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<MedicalRecordDto> createMedicalRecord(@Valid @RequestBody MedicalRecordDto medicalRecordDto) {
        try {
            MedicalRecordDto createdRecord = medicalRecordService.createMedicalRecord(medicalRecordDto);
            return ResponseEntity.ok(createdRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> updateMedicalRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecordDto medicalRecordDto) {
        try {
            MedicalRecordDto updatedRecord = medicalRecordService.updateMedicalRecord(id, medicalRecordDto);
            return ResponseEntity.ok(updatedRecord);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.ok().build();
    }
}