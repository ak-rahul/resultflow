package com.rahulak.srms.controller.rest;

import com.rahulak.srms.dto.StudentDTO;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.service.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for student CRUD operations.
 *
 * <pre>
 * GET    /api/students              → List&lt;StudentDTO&gt;
 * POST   /api/students              → create, 201 Created
 * GET    /api/students/{rollNo}     → StudentDTO
 * PUT    /api/students/{rollNo}     → update StudentDTO
 * DELETE /api/students/{rollNo}     → deactivate, 200 OK
 * </pre>
 */
@RestController
@RequestMapping("/api/students")
public class StudentRestController {

    private static final Logger log = LoggerFactory.getLogger(StudentRestController.class);

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String query) {
        log.debug("GET /api/students query='{}'", query);
        List<Student> students = (query != null && !query.isBlank())
            ? studentService.search(query)
            : studentService.findAll();
        List<StudentDTO> dtos = students.stream()
            .map(studentService::mapEntityToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO dto) {
        log.debug("POST /api/students rollNumber={}", dto.getRollNumber());
        Student saved = studentService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.mapEntityToDto(saved));
    }

    @GetMapping("/{rollNumber}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable String rollNumber) {
        log.debug("GET /api/students/{}", rollNumber);
        Student student = studentService.findByRollNumber(rollNumber);
        return ResponseEntity.ok(studentService.mapEntityToDto(student));
    }

    @PutMapping("/{rollNumber}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable String rollNumber,
                                                     @Valid @RequestBody StudentDTO dto) {
        log.debug("PUT /api/students/{}", rollNumber);
        Student student = studentService.findByRollNumber(rollNumber);
        Student updated = studentService.update(student.getId(), dto);
        return ResponseEntity.ok(studentService.mapEntityToDto(updated));
    }

    @DeleteMapping("/{rollNumber}")
    public ResponseEntity<String> deactivateStudent(@PathVariable String rollNumber) {
        log.debug("DELETE /api/students/{}", rollNumber);
        Student student = studentService.findByRollNumber(rollNumber);
        studentService.deactivate(student.getId());
        return ResponseEntity.ok("Student '" + rollNumber + "' deactivated successfully.");
    }
}
