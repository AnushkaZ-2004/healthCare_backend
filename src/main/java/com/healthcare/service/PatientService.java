package com.healthcare.service;

import com.healthcare.dto.PatientDto;
import com.healthcare.dto.UserDto;
import com.healthcare.model.Patient;
import com.healthcare.model.User;
import com.healthcare.model.Role;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<PatientDto> getActivePatients() {
        return patientRepository.findActivePatients().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PatientDto> getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<PatientDto> getPatientByPatientId(String patientId) {
        return patientRepository.findByPatientId(patientId)
                .map(this::convertToDto);
    }

    public PatientDto createPatient(PatientDto patientDto) {
        // Create user first
        User user = new User();
        user.setUsername(patientDto.getUser().getUsername());
        user.setEmail(patientDto.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(patientDto.getUser().getPassword()));
        user.setFirstName(patientDto.getUser().getFirstName());
        user.setLastName(patientDto.getUser().getLastName());
        user.setPhoneNumber(patientDto.getUser().getPhoneNumber());
        user.setRole(Role.PATIENT);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create patient
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setPatientId(generatePatientId());
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setGender(patientDto.getGender());
        patient.setAddress(patientDto.getAddress());
        patient.setEmergencyContact(patientDto.getEmergencyContact());
        patient.setBloodGroup(patientDto.getBloodGroup());
        patient.setAllergies(patientDto.getAllergies());
        patient.setMedicalHistory(patientDto.getMedicalHistory());

        Patient savedPatient = patientRepository.save(patient);
        return convertToDto(savedPatient);
    }

    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if (existingPatient.isEmpty()) {
            throw new RuntimeException("Patient not found");
        }

        Patient patient = existingPatient.get();
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setGender(patientDto.getGender());
        patient.setAddress(patientDto.getAddress());
        patient.setEmergencyContact(patientDto.getEmergencyContact());
        patient.setBloodGroup(patientDto.getBloodGroup());
        patient.setAllergies(patientDto.getAllergies());
        patient.setMedicalHistory(patientDto.getMedicalHistory());

        // Update user information
        User user = patient.getUser();
        user.setFirstName(patientDto.getUser().getFirstName());
        user.setLastName(patientDto.getUser().getLastName());
        user.setEmail(patientDto.getUser().getEmail());
        user.setPhoneNumber(patientDto.getUser().getPhoneNumber());

        userRepository.save(user);
        Patient updatedPatient = patientRepository.save(patient);
        return convertToDto(updatedPatient);
    }

    public void deletePatient(Long id) {
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent()) {
            Patient patient = patientOptional.get();
            User user = patient.getUser();
            patientRepository.deleteById(id);
            userRepository.deleteById(user.getId());
        }
    }

    private String generatePatientId() {
        long count = patientRepository.count();
        return "P" + String.format("%06d", count + 1);
    }

    private PatientDto convertToDto(Patient patient) {
        PatientDto patientDto = new PatientDto();
        patientDto.setId(patient.getId());
        patientDto.setPatientId(patient.getPatientId());
        patientDto.setDateOfBirth(patient.getDateOfBirth());
        patientDto.setGender(patient.getGender());
        patientDto.setAddress(patient.getAddress());
        patientDto.setEmergencyContact(patient.getEmergencyContact());
        patientDto.setBloodGroup(patient.getBloodGroup());
        patientDto.setAllergies(patient.getAllergies());
        patientDto.setMedicalHistory(patient.getMedicalHistory());

        // Convert user
        UserDto userDto = new UserDto();
        userDto.setId(patient.getUser().getId());
        userDto.setUsername(patient.getUser().getUsername());
        userDto.setEmail(patient.getUser().getEmail());
        userDto.setFirstName(patient.getUser().getFirstName());
        userDto.setLastName(patient.getUser().getLastName());
        userDto.setPhoneNumber(patient.getUser().getPhoneNumber());
        userDto.setRole(patient.getUser().getRole());
        userDto.setActive(patient.getUser().getActive());

        patientDto.setUser(userDto);
        return patientDto;
    }
}