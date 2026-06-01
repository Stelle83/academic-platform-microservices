package com.academic.assignmentservice.dto;

import com.academic.assignmentservice.model.Assignment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequest {

    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be at least 1")
    @Max(value = 5, message = "Grade cannot exceed 5")
    private Integer grade;

    @NotNull(message = "Feedback category is required")
    private Assignment.FeedbackCategory feedbackCategory;

    private String feedbackComment;
}