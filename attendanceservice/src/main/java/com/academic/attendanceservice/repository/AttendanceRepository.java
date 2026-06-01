package com.academic.attendanceservice.repository;

import com.academic.attendanceservice.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByStudentId(String studentId);
    List<Attendance> findByTeacherId(String teacherId);
    long countByStudentIdAndStatus(String studentId, Attendance.AttendanceStatus status);
    boolean existsByStudentIdAndDate(String studentId, java.time.LocalDate date);
}