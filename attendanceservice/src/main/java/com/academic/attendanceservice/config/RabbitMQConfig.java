package com.academic.attendanceservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class RabbitMQConfig {

    public static final String ATTENDANCE_EXCHANGE = "attendance.exchange";
    public static final String ATTENDANCE_RECORDED_QUEUE = "attendance-recorded";
    public static final String ATTENDANCE_WARNING_QUEUE = "attendance-warning";
    public static final String ATTENDANCE_RECORDED_KEY = "attendance.recorded";
    public static final String ATTENDANCE_WARNING_KEY = "attendance.warning";

    @Bean
    public TopicExchange attendanceExchange() {
        return new TopicExchange(ATTENDANCE_EXCHANGE);
    }

    @Bean
    public Queue attendanceRecordedQueue() {
        return new Queue(ATTENDANCE_RECORDED_QUEUE, true);
    }

    @Bean
    public Queue attendanceWarningQueue() {
        return new Queue(ATTENDANCE_WARNING_QUEUE, true);
    }

    @Bean
    public Binding attendanceRecordedBinding() {
        return BindingBuilder
                .bind(attendanceRecordedQueue())
                .to(attendanceExchange())
                .with(ATTENDANCE_RECORDED_KEY);
    }

    @Bean
    public Binding attendanceWarningBinding() {
        return BindingBuilder
                .bind(attendanceWarningQueue())
                .to(attendanceExchange())
                .with(ATTENDANCE_WARNING_KEY);
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("java.util.*", "com.academic.*"));
        return converter;
    }
}