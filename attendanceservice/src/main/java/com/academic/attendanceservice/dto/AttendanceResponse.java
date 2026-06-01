package com.academic.attendanceservice.dto;

import com.academic.attendanceservice.model.Attendance;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AttendanceResponse {
    private String id;
    private String studentId;
    private String teacherId;
    private LocalDate date;
    private Attendance.AttendanceStatus status;
}