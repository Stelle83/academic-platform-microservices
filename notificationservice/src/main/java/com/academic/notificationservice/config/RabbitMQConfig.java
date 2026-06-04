package com.academic.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    public static final String ASSIGNMENT_CREATED_QUEUE = "assignment-created";
    public static final String ASSIGNMENT_GRADED_QUEUE = "assignment-graded";
    public static final String ATTENDANCE_WARNING_QUEUE = "attendance-warning";

    // Declare queues so they exist even if other services haven't started yet
    @Bean
    public Queue assignmentCreatedQueue() {
        return new Queue(ASSIGNMENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue assignmentGradedQueue() {
        return new Queue(ASSIGNMENT_GRADED_QUEUE, true);
    }

    @Bean
    public Queue attendanceWarningQueue() {
        return new Queue(ATTENDANCE_WARNING_QUEUE, true);
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.util.*", "com.academic.*"));
        return converter;
    }
}