package com.academic.assignmentservice.dto;

import com.academic.assignmentservice.model.Assignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AssignmentResponse {
    private String id;
    private String title;
    private String description;
    private String teacherId;
    private String studentId;
    private LocalDate deadline;
    private Assignment.AssignmentStatus status;
    private Integer grade;
    private Assignment.FeedbackCategory feedbackCategory;
    private String feedbackComment;
}