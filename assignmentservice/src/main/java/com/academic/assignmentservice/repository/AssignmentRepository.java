package com.academic.assignmentservice.repository;

import com.academic.assignmentservice.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByStudentId(String studentId);
    List<Assignment> findByTeacherId(String teacherId);
    List<Assignment> findByStudentIdAndStatus(String studentId, Assignment.AssignmentStatus status);
}