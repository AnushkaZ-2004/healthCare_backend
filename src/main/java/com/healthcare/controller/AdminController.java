package com.healthcare.controller;

import com.healthcare.dto.UserDto;
import com.healthcare.dto.PatientDto;
import com.healthcare.dto.DoctorDto;
import com.healthcare.dto.AppointmentDto;
import com.healthcare.model.Role;
import com.healthcare.service.UserService;
import com.healthcare.service.PatientService;
import com.healthcare.service.DoctorService;
import com.healthcare.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Get counts
        List<UserDto> allUsers = userService.getAllUsers();
        List<PatientDto> allPatients = patientService.getAllPatients();
        List<DoctorDto> allDoctors = doctorService.getAllDoctors();
        List<AppointmentDto> allAppointments = appointmentService.getAllAppointments();
        List<AppointmentDto> todaysAppointments = appointmentService.getTodaysAppointments();

        dashboardData.put("totalUsers", allUsers.size());
        dashboardData.put("totalPatients", allPatients.size());
        dashboardData.put("totalDoctors", allDoctors.size());
        dashboardData.put("totalAppointments", allAppointments.size());
        dashboardData.put("todaysAppointments", todaysAppointments.size());

        // Get recent data
        dashboardData.put("recentPatients", allPatients.size() > 5 ?
                allPatients.subList(allPatients.size() - 5, allPatients.size()) : allPatients);
        dashboardData.put("recentAppointments", todaysAppointments);

        // Get users by role
        List<UserDto> admins = userService.getUsersByRole(Role.ADMIN);
        List<UserDto> doctors = userService.getUsersByRole(Role.DOCTOR);
        List<UserDto> patients = userService.getUsersByRole(Role.PATIENT);

        dashboardData.put("adminCount", admins.size());
        dashboardData.put("doctorCount", doctors.size());
        dashboardData.put("patientCount", patients.size());

        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Integer>> getUserStats() {
        Map<String, Integer> stats = new HashMap<>();

        List<UserDto> admins = userService.getUsersByRole(Role.ADMIN);
        List<UserDto> doctors = userService.getUsersByRole(Role.DOCTOR);
        List<UserDto> patients = userService.getUsersByRole(Role.PATIENT);

        stats.put("admins", admins.size());
        stats.put("doctors", doctors.size());
        stats.put("patients", patients.size());
        stats.put("total", admins.size() + doctors.size() + patients.size());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/appointments/stats")
    public ResponseEntity<Map<String, Integer>> getAppointmentStats() {
        Map<String, Integer> stats = new HashMap<>();

        List<AppointmentDto> scheduled = appointmentService.getAppointmentsByStatus("SCHEDULED");
        List<AppointmentDto> completed = appointmentService.getAppointmentsByStatus("COMPLETED");
        List<AppointmentDto> cancelled = appointmentService.getAppointmentsByStatus("CANCELLED");
        List<AppointmentDto> today = appointmentService.getTodaysAppointments();

        stats.put("scheduled", scheduled.size());
        stats.put("completed", completed.size());
        stats.put("cancelled", cancelled.size());
        stats.put("today", today.size());
        stats.put("total", scheduled.size() + completed.size() + cancelled.size());

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<UserDto> createAdmin(@RequestBody UserDto userDto) {
        try {
            userDto.setRole(Role.ADMIN);
            UserDto createdAdmin = userService.createUser(userDto);
            return ResponseEntity.ok(createdAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}