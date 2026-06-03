package com.academic.assignmentservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AssignmentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String studentId;

    private List<String> studentIds;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    private boolean sendReminder = true;
}