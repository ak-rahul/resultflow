package com.rahulak.srms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Student Result Management System (SRMS).
 *
 * <p>Tech stack: Spring Boot 3.2 · Spring MVC (Thymeleaf) · Spring REST ·
 * Spring Data JPA · Spring JDBC (JdbcTemplate) · MySQL 8
 *
 * <p>Run with: {@code mvn spring-boot:run}
 */
@SpringBootApplication
public class SrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrmsApplication.class, args);
    }
}
