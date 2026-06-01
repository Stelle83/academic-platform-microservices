package com.academic.assignmentservice.controller;

import com.academic.assignmentservice.dto.*;
import com.academic.assignmentservice.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentResponse> createAssignment(
            @RequestHeader("X-User-Id") String teacherId,
            @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.createAssignment(teacherId, request));
    }

    @PutMapping("/{assignmentId}/grade")
    public ResponseEntity<AssignmentResponse> gradeAssignment(
            @PathVariable String assignmentId,
            @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(assignmentService.gradeAssignment(assignmentId, request));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssignmentResponse>> getStudentAssignments(
            @PathVariable String studentId) {
        return ResponseEntity.ok(assignmentService.getStudentAssignments(studentId));
    }

    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<AssignmentResponse>> getActiveAssignments(
            @PathVariable String studentId) {
        return ResponseEntity.ok(assignmentService.getActiveAssignments(studentId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<AssignmentResponse>> getTeacherAssignments(
            @PathVariable String teacherId) {
        return ResponseEntity.ok(assignmentService.getTeacherAssignments(teacherId));
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<AssignmentResponse> getAssignment(
            @PathVariable String assignmentId) {
        return ResponseEntity.ok(assignmentService.getAssignment(assignmentId));
    }
}