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

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.util.*", "com.academic.*"));
        return converter;
    }
}