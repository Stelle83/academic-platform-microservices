package com.academic.attendanceservice.controller;

import com.academic.attendanceservice.dto.*;
import com.academic.attendanceservice.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.recordAttendance(request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @PathVariable String studentId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId));
    }

    @GetMapping("/student/{studentId}/stats")
    public ResponseEntity<AttendanceStatsResponse> getStudentStats(
            @PathVariable String studentId) {
        return ResponseEntity.ok(attendanceService.getStudentStats(studentId));
    }
}
