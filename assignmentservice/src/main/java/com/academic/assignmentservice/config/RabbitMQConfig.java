package com.academic.assignmentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    public static final String ASSIGNMENT_EXCHANGE = "assignment.exchange";
    public static final String ASSIGNMENT_CREATED_QUEUE = "assignment-created";
    public static final String ASSIGNMENT_GRADED_QUEUE = "assignment-graded";
    public static final String ASSIGNMENT_CREATED_KEY = "assignment.created";
    public static final String ASSIGNMENT_GRADED_KEY = "assignment.graded";

    @Bean
    public TopicExchange assignmentExchange() {
        return new TopicExchange(ASSIGNMENT_EXCHANGE);
    }

    @Bean
    public Queue assignmentCreatedQueue() {
        return new Queue(ASSIGNMENT_CREATED_QUEUE, true);
    }

    @Bean
    public Queue assignmentGradedQueue() {
        return new Queue(ASSIGNMENT_GRADED_QUEUE, true);
    }

    @Bean
    public Binding assignmentCreatedBinding() {
        return BindingBuilder
                .bind(assignmentCreatedQueue())
                .to(assignmentExchange())
                .with(ASSIGNMENT_CREATED_KEY);
    }

    @Bean
    public Binding assignmentGradedBinding() {
        return BindingBuilder
                .bind(assignmentGradedQueue())
                .to(assignmentExchange())
                .with(ASSIGNMENT_GRADED_KEY);
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.util.*", "com.academic.*"));
        return converter;
    }
}