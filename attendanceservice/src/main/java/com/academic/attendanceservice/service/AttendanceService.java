package com.academic.attendanceservice.service;

import com.academic.attendanceservice.config.RabbitMQConfig;
import com.academic.attendanceservice.dto.*;
import com.academic.attendanceservice.model.Attendance;
import com.academic.attendanceservice.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${attendance.absence.warning-threshold}")
    private int warningThreshold;

    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        if (attendanceRepository.existsByStudentIdAndDate(
                request.getStudentId(), request.getDate())) {
            throw new RuntimeException("Attendance already recorded for this student today");
        }

        Attendance attendance = Attendance.builder()
                .studentId(request.getStudentId())
                .teacherId(request.getTeacherId())
                .date(request.getDate())
                .status(request.getStatus())
                .build();

        attendanceRepository.save(attendance);
        log.info("Attendance recorded for student: {}", request.getStudentId());

        // Publish attendance-recorded event
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ATTENDANCE_EXCHANGE,
                RabbitMQConfig.ATTENDANCE_RECORDED_KEY,
                Map.of(
                        "studentId", request.getStudentId(),
                        "status", request.getStatus().name(),
                        "date", request.getDate().toString()
                )
        );

        // Check absence threshold — our "bot" logic!
        if (request.getStatus() == Attendance.AttendanceStatus.ABSENT) {
            checkAndPublishWarning(request.getStudentId());
        }

        return mapToResponse(attendance);
    }

    private void checkAndPublishWarning(String studentId) {
        long absenceCount = attendanceRepository.countByStudentIdAndStatus(
                studentId, Attendance.AttendanceStatus.ABSENT);

        log.info("Student {} has {} absences", studentId, absenceCount);

        if (absenceCount >= warningThreshold) {
            log.warn("Publishing attendance warning for student: {}", studentId);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ATTENDANCE_EXCHANGE,
                    RabbitMQConfig.ATTENDANCE_WARNING_KEY,
                    Map.of(
                            "studentId", studentId,
                            "absenceCount", absenceCount,
                            "message", "Student has reached the absence warning threshold"
                    )
            );
        }
    }

    public List<AttendanceResponse> getStudentAttendance(String studentId) {
        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AttendanceStatsResponse getStudentStats(String studentId) {
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);
        long total = records.size();
        long present = attendanceRepository.countByStudentIdAndStatus(
                studentId, Attendance.AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStudentIdAndStatus(
                studentId, Attendance.AttendanceStatus.ABSENT);
        long late = attendanceRepository.countByStudentIdAndStatus(
                studentId, Attendance.AttendanceStatus.LATE);
        long sick = attendanceRepository.countByStudentIdAndStatus(
                studentId, Attendance.AttendanceStatus.SICK);

        double percentage = total > 0
                ? Math.round(((double)(present + late) / total) * 100.0)
                : 0.0;

        return new AttendanceStatsResponse(
                studentId, total, present, absent, late, sick, percentage);
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getStudentId(),
                attendance.getTeacherId(),
                attendance.getDate(),
                attendance.getStatus()
        );
    }
}