package com.academic.attendanceservice.dto;

import com.academic.attendanceservice.model.Attendance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceRequest {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Teacher ID is required")
    private String teacherId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private Attendance.AttendanceStatus status;
}