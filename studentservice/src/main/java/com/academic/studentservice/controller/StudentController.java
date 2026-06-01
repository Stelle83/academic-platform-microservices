package com.academic.studentservice.controller;

import com.academic.studentservice.dto.*;
import com.academic.studentservice.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(userId, request));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(
            @PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getStudent(studentId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByTeacher(
            @PathVariable String teacherId) {
        return ResponseEntity.ok(studentService.getStudentsByTeacher(teacherId));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable String studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}