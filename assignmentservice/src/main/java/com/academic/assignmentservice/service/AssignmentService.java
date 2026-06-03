package com.academic.assignmentservice.service;

import com.academic.assignmentservice.config.RabbitMQConfig;
import com.academic.assignmentservice.dto.*;
        import com.academic.assignmentservice.model.Assignment;
import com.academic.assignmentservice.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final RabbitTemplate rabbitTemplate;

    public AssignmentResponse createAssignment(String teacherId, AssignmentRequest request) {
        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacherId(teacherId)
                .studentId(request.getStudentId())
                .deadline(request.getDeadline())
                .status(Assignment.AssignmentStatus.ACTIVE)
                .build();

        assignmentRepository.save(assignment);
        log.info("Assignment created: {}", assignment.getId());

        // Publish assignment-created event
        if (request.isSendReminder()) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ASSIGNMENT_EXCHANGE,
                    RabbitMQConfig.ASSIGNMENT_CREATED_KEY,
                    Map.of(
                            "assignmentId", assignment.getId(),
                            "title", assignment.getTitle(),
                            "studentId", assignment.getStudentId(),
                            "teacherId", assignment.getTeacherId(),
                            "deadline", assignment.getDeadline().toString()
                    )
            );
            log.info("Published assignment-created event for: {}", assignment.getId());
        }

        return mapToResponse(assignment);
    }

    public List<AssignmentResponse> createAssignmentForAll(
            String teacherId, AssignmentRequest request, List<String> studentIds) {

        List<AssignmentResponse> responses = new ArrayList<>();

        for (String studentId : studentIds) {
            Assignment assignment = Assignment.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .teacherId(teacherId)
                    .studentId(studentId)
                    .deadline(request.getDeadline())
                    .status(Assignment.AssignmentStatus.ACTIVE)
                    .build();

            assignmentRepository.save(assignment);

            if (request.isSendReminder()) {
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ASSIGNMENT_EXCHANGE,
                        RabbitMQConfig.ASSIGNMENT_CREATED_KEY,
                        Map.of(
                                "assignmentId", assignment.getId(),
                                "title", assignment.getTitle(),
                                "studentId", studentId,
                                "teacherId", teacherId,
                                "deadline", assignment.getDeadline().toString()
                        )
                );
            }
            responses.add(mapToResponse(assignment));
        }

        log.info("Assignment created for {} students", studentIds.size());
        return responses;
    }

    public AssignmentResponse gradeAssignment(String assignmentId, GradeRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setGrade(request.getGrade());
        assignment.setFeedbackCategory(request.getFeedbackCategory());
        assignment.setFeedbackComment(request.getFeedbackComment());
        assignment.setStatus(Assignment.AssignmentStatus.GRADED);

        assignmentRepository.save(assignment);
        log.info("Assignment graded: {}", assignmentId);

        // Publish assignment-graded event
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ASSIGNMENT_EXCHANGE,
                RabbitMQConfig.ASSIGNMENT_GRADED_KEY,
                Map.of(
                        "assignmentId", assignment.getId(),
                        "studentId", assignment.getStudentId(),
                        "teacherId", assignment.getTeacherId(),
                        "grade", request.getGrade(),
                        "feedbackCategory", request.getFeedbackCategory().name()
                )
        );

        return mapToResponse(assignment);
    }

    public List<AssignmentResponse> getStudentAssignments(String studentId) {
        return assignmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AssignmentResponse> getTeacherAssignments(String teacherId) {
        return assignmentRepository.findByTeacherId(teacherId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AssignmentResponse> getActiveAssignments(String studentId) {
        return assignmentRepository.findByStudentIdAndStatus(
                        studentId, Assignment.AssignmentStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AssignmentResponse getAssignment(String assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        return mapToResponse(assignment);
    }

    public AssignmentResponse submitAssignment(
            String assignmentId, SubmitRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getStatus() == Assignment.AssignmentStatus.GRADED) {
            throw new RuntimeException("Cannot submit a graded assignment");
        }

        assignment.setStatus(Assignment.AssignmentStatus.SUBMITTED);
        assignment.setSubmissionComment(request.getComment());
        assignmentRepository.save(assignment);

        log.info("Assignment submitted: {}", assignmentId);

        // Publish event so teacher gets notified
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ASSIGNMENT_EXCHANGE,
                RabbitMQConfig.ASSIGNMENT_GRADED_KEY,
                Map.of(
                        "assignmentId", assignment.getId(),
                        "studentId", assignment.getStudentId(),
                        "teacherId", assignment.getTeacherId(),
                        "title", assignment.getTitle(),
                        "event", "SUBMITTED"
                )
        );

        return mapToResponse(assignment);
    }

    private AssignmentResponse mapToResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getTeacherId(),
                assignment.getStudentId(),
                assignment.getDeadline(),
                assignment.getStatus(),
                assignment.getGrade(),
                assignment.getFeedbackCategory(),
                assignment.getFeedbackComment(),
                assignment.getSubmissionComment()
        );
    }
}