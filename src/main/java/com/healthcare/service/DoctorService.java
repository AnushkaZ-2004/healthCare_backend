package com.healthcare.service;

import com.healthcare.dto.DoctorDto;
import com.healthcare.dto.UserDto;
import com.healthcare.model.Doctor;
import com.healthcare.model.User;
import com.healthcare.model.Role;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorDto> getActiveDoctors() {
        return doctorRepository.findActiveDoctors().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorDto> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoctorDto> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<DoctorDto> getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(this::convertToDto);
    }

    public Optional<DoctorDto> getDoctorByDoctorId(String doctorId) {
        return doctorRepository.findByDoctorId(doctorId)
                .map(this::convertToDto);
    }

    public DoctorDto createDoctor(DoctorDto doctorDto) {
        // Create user first
        User user = new User();
        user.setUsername(doctorDto.getUser().getUsername());
        user.setEmail(doctorDto.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(doctorDto.getUser().getPassword()));
        user.setFirstName(doctorDto.getUser().getFirstName());
        user.setLastName(doctorDto.getUser().getLastName());
        user.setPhoneNumber(doctorDto.getUser().getPhoneNumber());
        user.setRole(Role.DOCTOR);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // Create doctor
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setDoctorId(generateDoctorId());
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setQualification(doctorDto.getQualification());
        doctor.setExperience(doctorDto.getExperience());
        doctor.setDepartment(doctorDto.getDepartment());
        doctor.setConsultationFee(doctorDto.getConsultationFee());
        doctor.setWorkingHours(doctorDto.getWorkingHours());
        doctor.setAvailable(doctorDto.getAvailable() != null ? doctorDto.getAvailable() : true);

        Doctor savedDoctor = doctorRepository.save(doctor);
        return convertToDto(savedDoctor);
    }

    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        Optional<Doctor> existingDoctor = doctorRepository.findById(id);
        if (existingDoctor.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }

        Doctor doctor = existingDoctor.get();
        doctor.setSpecialization(doctorDto.getSpecialization());
        doctor.setQualification(doctorDto.getQualification());
        doctor.setExperience(doctorDto.getExperience());
        doctor.setDepartment(doctorDto.getDepartment());
        doctor.setConsultationFee(doctorDto.getConsultationFee());
        doctor.setWorkingHours(doctorDto.getWorkingHours());
        doctor.setAvailable(doctorDto.getAvailable());

        // Update user information
        User user = doctor.getUser();
        user.setFirstName(doctorDto.getUser().getFirstName());
        user.setLastName(doctorDto.getUser().getLastName());
        user.setEmail(doctorDto.getUser().getEmail());
        user.setPhoneNumber(doctorDto.getUser().getPhoneNumber());

        userRepository.save(user);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return convertToDto(updatedDoctor);
    }

    public void deleteDoctor(Long id) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(id);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            User user = doctor.getUser();
            doctorRepository.deleteById(id);
            userRepository.deleteById(user.getId());
        }
    }

    private String generateDoctorId() {
        long count = doctorRepository.count();
        return "D" + String.format("%06d", count + 1);
    }

    private DoctorDto convertToDto(Doctor doctor) {
        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setId(doctor.getId());
        doctorDto.setDoctorId(doctor.getDoctorId());
        doctorDto.setSpecialization(doctor.getSpecialization());
        doctorDto.setQualification(doctor.getQualification());
        doctorDto.setExperience(doctor.getExperience());
        doctorDto.setDepartment(doctor.getDepartment());
        doctorDto.setConsultationFee(doctor.getConsultationFee());
        doctorDto.setWorkingHours(doctor.getWorkingHours());
        doctorDto.setAvailable(doctor.getAvailable());

        // Convert user
        UserDto userDto = new UserDto();
        userDto.setId(doctor.getUser().getId());
        userDto.setUsername(doctor.getUser().getUsername());
        userDto.setEmail(doctor.getUser().getEmail());
        userDto.setFirstName(doctor.getUser().getFirstName());
        userDto.setLastName(doctor.getUser().getLastName());
        userDto.setPhoneNumber(doctor.getUser().getPhoneNumber());
        userDto.setRole(doctor.getUser().getRole());
        userDto.setActive(doctor.getUser().getActive());

        doctorDto.setUser(userDto);
        return doctorDto;
    }
}