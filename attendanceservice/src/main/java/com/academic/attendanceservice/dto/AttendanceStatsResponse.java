package com.academic.attendanceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceStatsResponse {
    private String studentId;
    private long totalLessons;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private long sickCount;
    private double attendancePercentage;
}