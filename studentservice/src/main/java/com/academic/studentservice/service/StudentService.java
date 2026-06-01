package com.academic.studentservice.service;

import com.academic.studentservice.dto.*;
import com.academic.studentservice.model.Student;

import com.academic.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentResponse createStudent(String userId, StudentRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Student student = Student.builder()
                .id(userId)
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role("STUDENT")
                .teacherId(request.getTeacherId())
                .build();

        studentRepository.save(student);
        return mapToResponse(student);
    }

    public StudentResponse getStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return mapToResponse(student);
    }

    public List<StudentResponse> getStudentsByTeacher(String teacherId) {
        return studentRepository.findByTeacherId(teacherId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public StudentResponse updateStudent(String studentId, StudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setTeacherId(request.getTeacherId());

        studentRepository.save(student);
        return mapToResponse(student);
    }

    public void deleteStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found");
        }
        studentRepository.deleteById(studentId);
    }

    private StudentResponse mapToResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getUsername(),
                student.getEmail(),
                student.getFullName(),
                student.getRole(),
                student.getTeacherId()
        );
    }
}