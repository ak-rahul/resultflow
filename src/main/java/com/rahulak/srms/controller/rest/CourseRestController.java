package com.rahulak.srms.controller.rest;

import com.rahulak.srms.dto.CourseDTO;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.service.CourseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for course operations.
 *
 * <pre>
 * GET  /api/courses             → all courses
 * POST /api/courses             → create course (201)
 * GET  /api/courses/{code}      → single course by code
 * </pre>
 */
@RestController
@RequestMapping("/api/courses")
public class CourseRestController {

    private static final Logger log = LoggerFactory.getLogger(CourseRestController.class);

    private final CourseService courseService;

    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(
            @RequestParam(required = false) String department) {
        log.debug("GET /api/courses department='{}'", department);
        List<Course> courses = (department != null && !department.isBlank())
            ? courseService.findByDepartment(department)
            : courseService.findAll();
        List<CourseDTO> dtos = courses.stream()
            .map(courseService::mapEntityToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO dto) {
        log.debug("POST /api/courses courseCode={}", dto.getCourseCode());
        Course saved = courseService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.mapEntityToDto(saved));
    }

    @GetMapping("/{courseCode}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable String courseCode) {
        log.debug("GET /api/courses/{}", courseCode);
        Course course = courseService.findByCourseCode(courseCode);
        return ResponseEntity.ok(courseService.mapEntityToDto(course));
    }
}
