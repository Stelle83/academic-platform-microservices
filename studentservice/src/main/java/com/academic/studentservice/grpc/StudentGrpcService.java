package com.academic.studentservice.grpc;

import com.academic.grpc.student.*;
import com.academic.studentservice.repository.StudentRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class StudentGrpcService extends StudentServiceGrpc.StudentServiceImplBase {

    private final StudentRepository studentRepository;

    @Override
    public void getStudent(GetStudentRequest request,
                           StreamObserver<StudentResponse> responseObserver) {
        studentRepository.findById(request.getStudentId())
                .ifPresentOrElse(
                        student -> {
                            StudentResponse response = StudentResponse.newBuilder()
                                    .setId(student.getId())
                                    .setUsername(student.getUsername())
                                    .setEmail(student.getEmail())
                                    .setFullName(student.getFullName())
                                    .build();
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        () -> responseObserver.onError(
                                new RuntimeException("Student not found")
                        )
                );
    }

    @Override
    public void studentExists(GetStudentRequest request,
                              StreamObserver<StudentExistsResponse> responseObserver) {
        boolean exists = studentRepository.existsById(request.getStudentId());
        StudentExistsResponse response = StudentExistsResponse.newBuilder()
                .setExists(exists)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}