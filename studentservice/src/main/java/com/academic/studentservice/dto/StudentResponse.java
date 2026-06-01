package com.academic.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentResponse {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String teacherId;
}
