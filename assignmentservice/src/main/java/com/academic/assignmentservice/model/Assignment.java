package com.academic.assignmentservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String teacherId;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    private Integer grade;

    @Enumerated(EnumType.STRING)
    private FeedbackCategory feedbackCategory;

    @Column(length = 500)
    private String feedbackComment;

    @Column(length = 1000)
    private String submissionComment;

    public enum AssignmentStatus {
        ACTIVE, SUBMITTED, GRADED
    }

    public enum FeedbackCategory {
        DOCUMENTATION,
        CONSTRUCTION,
        INDUSTRIALIZATION,
        FITTING,
        COMMUNICATION,
        TIME_MANAGEMENT
    }
}