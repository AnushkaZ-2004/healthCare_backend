package com.healthcare.service;

import com.healthcare.dto.AppointmentDto;
import com.healthcare.model.Appointment;
import com.healthcare.model.Patient;
import com.healthcare.model.Doctor;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getTodaysAppointments() {
        return appointmentRepository.findTodaysAppointments().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentDto> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atTime(LocalTime.MIN);
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<AppointmentDto> getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDto);
    }

    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        Optional<Patient> patient = patientRepository.findById(appointmentDto.getPatientId());
        Optional<Doctor> doctor = doctorRepository.findById(appointmentDto.getDoctorId());

        if (patient.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }
        if (doctor.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        // Check for conflicting appointments
        List<Appointment> conflictingAppointments = appointmentRepository
                .findByDoctorIdAndDateRange(
                        appointmentDto.getDoctorId(),
                        appointmentDto.getAppointmentDateTime().minusHours(1),
                        appointmentDto.getAppointmentDateTime().plusHours(1)
                );

        if (!conflictingAppointments.isEmpty()) {
            throw new RuntimeException("Doctor is not available at this time");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointment.setAppointmentDateTime(appointmentDto.getAppointmentDateTime());
        appointment.setReason(appointmentDto.getReason());
        appointment.setStatus("SCHEDULED");

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToDto(savedAppointment);
    }

    public AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto) {
        Optional<Appointment> existingAppointment = appointmentRepository.findById(id);
        if (existingAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = existingAppointment.get();
        appointment.setAppointmentDateTime(appointmentDto.getAppointmentDateTime());
        appointment.setReason(appointmentDto.getReason());
        appointment.setStatus(appointmentDto.getStatus());
        appointment.setNotes(appointmentDto.getNotes());
        appointment.setPrescription(appointmentDto.getPrescription());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDto(updatedAppointment);
    }

    public AppointmentDto updateAppointmentStatus(Long id, String status) {
        Optional<Appointment> existingAppointment = appointmentRepository.findById(id);
        if (existingAppointment.isEmpty()) {
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = existingAppointment.get();
        appointment.setStatus(status);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDto(updatedAppointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentDto convertToDto(Appointment appointment) {
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setId(appointment.getId());
        appointmentDto.setPatientId(appointment.getPatient().getId());
        appointmentDto.setDoctorId(appointment.getDoctor().getId());
        appointmentDto.setPatientName(appointment.getPatient().getUser().getFirstName() + " " +
                appointment.getPatient().getUser().getLastName());
        appointmentDto.setDoctorName(appointment.getDoctor().getUser().getFirstName() + " " +
                appointment.getDoctor().getUser().getLastName());
        appointmentDto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        appointmentDto.setReason(appointment.getReason());
        appointmentDto.setStatus(appointment.getStatus());
        appointmentDto.setNotes(appointment.getNotes());
        appointmentDto.setPrescription(appointment.getPrescription());
        return appointmentDto;
    }
}