package com.academic.studentservice.repository;

import com.academic.studentservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByUsername(String username);
    List<Student> findByTeacherId(String teacherId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}