package com.healthcare.service;

import com.healthcare.dto.MedicalRecordDto;
import com.healthcare.model.MedicalRecord;
import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import com.healthcare.model.Appointment;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public List<MedicalRecordDto> getAllMedicalRecords() {
        return medicalRecordRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> getMedicalRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> getMedicalRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atTime(LocalTime.MIN);
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return medicalRecordRepository.findByVisitDateBetween(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDto> searchByDiagnosis(String diagnosis) {
        return medicalRecordRepository.findByDiagnosisContaining(diagnosis).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<MedicalRecordDto> getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<MedicalRecordDto> getMedicalRecordByAppointment(Long appointmentId) {
        return medicalRecordRepository.findByAppointmentId(appointmentId)
                .map(this::convertToDto);
    }

    public MedicalRecordDto createMedicalRecord(MedicalRecordDto medicalRecordDto) {
        Optional<Patient> patient = patientRepository.findById(medicalRecordDto.getPatientId());
        Optional<Doctor> doctor = doctorRepository.findById(medicalRecordDto.getDoctorId());

        if (patient.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }
        if (doctor.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient.get());
        medicalRecord.setDoctor(doctor.get());

        // Set appointment if provided
        if (medicalRecordDto.getAppointmentId() != null) {
            Optional<Appointment> appointment = appointmentRepository.findById(medicalRecordDto.getAppointmentId());
            appointment.ifPresent(medicalRecord::setAppointment);
        }

        medicalRecord.setDiagnosis(medicalRecordDto.getDiagnosis());
        medicalRecord.setSymptoms(medicalRecordDto.getSymptoms());
        medicalRecord.setTreatment(medicalRecordDto.getTreatment());
        medicalRecord.setPrescription(medicalRecordDto.getPrescription());
        medicalRecord.setTestResults(medicalRecordDto.getTestResults());
        medicalRecord.setNotes(medicalRecordDto.getNotes());

        if (medicalRecordDto.getVisitDate() != null) {
            medicalRecord.setVisitDate(medicalRecordDto.getVisitDate());
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return convertToDto(savedRecord);
    }

    public MedicalRecordDto updateMedicalRecord(Long id, MedicalRecordDto medicalRecordDto) {
        Optional<MedicalRecord> existingRecord = medicalRecordRepository.findById(id);
        if (existingRecord.isEmpty()) {
            throw new RuntimeException("Medical record not found");
        }

        MedicalRecord medicalRecord = existingRecord.get();
        medicalRecord.setDiagnosis(medicalRecordDto.getDiagnosis());
        medicalRecord.setSymptoms(medicalRecordDto.getSymptoms());
        medicalRecord.setTreatment(medicalRecordDto.getTreatment());
        medicalRecord.setPrescription(medicalRecordDto.getPrescription());
        medicalRecord.setTestResults(medicalRecordDto.getTestResults());
        medicalRecord.setNotes(medicalRecordDto.getNotes());

        if (medicalRecordDto.getVisitDate() != null) {
            medicalRecord.setVisitDate(medicalRecordDto.getVisitDate());
        }

        MedicalRecord updatedRecord = medicalRecordRepository.save(medicalRecord);
        return convertToDto(updatedRecord);
    }

    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDto convertToDto(MedicalRecord medicalRecord) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(medicalRecord.getId());
        dto.setPatientId(medicalRecord.getPatient().getId());
        dto.setDoctorId(medicalRecord.getDoctor().getId());

        if (medicalRecord.getAppointment() != null) {
            dto.setAppointmentId(medicalRecord.getAppointment().getId());
        }

        dto.setPatientName(medicalRecord.getPatient().getUser().getFirstName() + " " +
                medicalRecord.getPatient().getUser().getLastName());
        dto.setDoctorName(medicalRecord.getDoctor().getUser().getFirstName() + " " +
                medicalRecord.getDoctor().getUser().getLastName());
        dto.setDiagnosis(medicalRecord.getDiagnosis());
        dto.setSymptoms(medicalRecord.getSymptoms());
        dto.setTreatment(medicalRecord.getTreatment());
        dto.setPrescription(medicalRecord.getPrescription());
        dto.setTestResults(medicalRecord.getTestResults());
        dto.setNotes(medicalRecord.getNotes());
        dto.setVisitDate(medicalRecord.getVisitDate());

        return dto;
    }
}
