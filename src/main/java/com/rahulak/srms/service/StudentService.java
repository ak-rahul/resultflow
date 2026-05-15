package com.rahulak.srms.service;

import com.rahulak.srms.dto.StudentDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for student CRUD operations.
 *
 * <p>Soft-delete pattern: deactivation sets {@code isActive = false} rather than
 * removing the row, preserving historical grade records.
 */
@Service
@Transactional
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    /** Returns all students (active and inactive) ordered by name. */
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        log.debug("findAll() invoked");
        List<Student> students = studentRepository.findAll();
        log.debug("findAll() returning {} students", students.size());
        return students;
    }

    /** Returns all active students. */
    @Transactional(readOnly = true)
    public List<Student> findAllActive() {
        log.debug("findAllActive() invoked");
        return studentRepository.findByIsActiveTrue();
    }

    /**
     * Finds a student by their roll number.
     *
     * @throws ResourceNotFoundException if no student exists with that roll number
     */
    @Transactional(readOnly = true)
    public Student findByRollNumber(String rollNumber) {
        log.debug("findByRollNumber({})", rollNumber);
        return studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> {
                log.warn("Student not found with roll number: {}", rollNumber);
                return new ResourceNotFoundException("Student", "roll number", rollNumber);
            });
    }

    /**
     * Finds a student by their internal ID.
     *
     * @throws ResourceNotFoundException if no student with the given id exists
     */
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        log.debug("findById({})", id);
        return studentRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Student not found with id: {}", id);
                return new ResourceNotFoundException("Student", "id", id);
            });
    }

    /**
     * Searches students by a query string matched against roll number OR name.
     *
     * @param query partial roll number or name fragment (case-insensitive)
     * @return list of matching students
     */
    @Transactional(readOnly = true)
    public List<Student> search(String query) {
        log.debug("search('{}')", query);
        return studentRepository
            .findByRollNumberContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    /**
     * Saves a new student from a DTO.
     *
     * @throws IllegalArgumentException if roll number is already taken
     */
    public Student save(StudentDTO dto) {
        log.debug("save() — rollNumber={}", dto.getRollNumber());

        if (studentRepository.existsByRollNumber(dto.getRollNumber())) {
            log.warn("Duplicate roll number: {}", dto.getRollNumber());
            throw new IllegalArgumentException(
                "A student with roll number '" + dto.getRollNumber() + "' already exists");
        }

        Student student = mapDtoToEntity(dto, new Student());
        Student saved = studentRepository.save(student);
        log.info("Student saved: id={}, rollNumber={}", saved.getId(), saved.getRollNumber());
        return saved;
    }

    /**
     * Updates an existing student (found by id) from the given DTO.
     *
     * @throws ResourceNotFoundException if student is not found
     */
    public Student update(Long id, StudentDTO dto) {
        log.debug("update() — id={}", id);
        Student existing = findById(id);
        mapDtoToEntity(dto, existing);
        Student updated = studentRepository.save(existing);
        log.info("Student updated: id={}", updated.getId());
        return updated;
    }

    /**
     * Soft-deactivates a student (sets isActive = false).
     * Grades and enrollment history are preserved.
     *
     * @throws ResourceNotFoundException if student is not found
     */
    public void deactivate(Long id) {
        log.debug("deactivate() — id={}", id);
        Student student = findById(id);
        student.setIsActive(false);
        studentRepository.save(student);
        log.info("Student deactivated: id={}, rollNumber={}", id, student.getRollNumber());
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    /** Maps fields from a DTO onto a Student entity (create or update). */
    private Student mapDtoToEntity(StudentDTO dto, Student student) {
        student.setRollNumber(dto.getRollNumber());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setPhone(dto.getPhone());
        student.setDepartment(dto.getDepartment());
        student.setCurrentSem(dto.getCurrentSem() != null ? dto.getCurrentSem() : 1);
        student.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getIsActive() != null) {
            student.setIsActive(dto.getIsActive());
        }
        return student;
    }

    /** Maps a Student entity to a StudentDTO (for REST responses). */
    public StudentDTO mapEntityToDto(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setRollNumber(student.getRollNumber());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setPhone(student.getPhone());
        dto.setDepartment(student.getDepartment());
        dto.setCurrentSem(student.getCurrentSem());
        dto.setDateOfBirth(student.getDateOfBirth());
        dto.setIsActive(student.getIsActive());
        return dto;
    }
}
