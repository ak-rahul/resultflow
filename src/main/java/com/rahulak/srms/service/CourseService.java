package com.rahulak.srms.service;

import com.rahulak.srms.dto.CourseDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.CourseType;
import com.rahulak.srms.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for course CRUD operations.
 */
@Service
@Transactional
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        log.debug("findAll() invoked");
        return courseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Course> findAllActive() {
        log.debug("findAllActive() invoked");
        return courseRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Course> findByDepartment(String department) {
        log.debug("findByDepartment('{}')", department);
        return courseRepository.findByDepartment(department);
    }

    @Transactional(readOnly = true)
    public Course findById(Long id) {
        log.debug("findById({})", id);
        return courseRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Course not found with id: {}", id);
                return new ResourceNotFoundException("Course", "id", id);
            });
    }

    @Transactional(readOnly = true)
    public Course findByCourseCode(String courseCode) {
        log.debug("findByCourseCode('{}')", courseCode);
        return courseRepository.findByCourseCode(courseCode)
            .orElseThrow(() -> {
                log.warn("Course not found with code: {}", courseCode);
                return new ResourceNotFoundException("Course", "code", courseCode);
            });
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    public Course save(CourseDTO dto) {
        log.debug("save() — courseCode={}", dto.getCourseCode());

        if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
            throw new IllegalArgumentException(
                "A course with code '" + dto.getCourseCode() + "' already exists");
        }

        Course course = mapDtoToEntity(dto, new Course());
        Course saved = courseRepository.save(course);
        log.info("Course saved: id={}, code={}", saved.getId(), saved.getCourseCode());
        return saved;
    }

    public Course update(Long id, CourseDTO dto) {
        log.debug("update() — id={}", id);
        Course existing = findById(id);
        mapDtoToEntity(dto, existing);
        Course updated = courseRepository.save(existing);
        log.info("Course updated: id={}", updated.getId());
        return updated;
    }

    public void deactivate(Long id) {
        log.debug("deactivate() — id={}", id);
        Course course = findById(id);
        course.setIsActive(false);
        courseRepository.save(course);
        log.info("Course deactivated: id={}", id);
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private Course mapDtoToEntity(CourseDTO dto, Course course) {
        course.setCourseCode(dto.getCourseCode());
        course.setCourseName(dto.getCourseName());
        course.setCredits(dto.getCredits());
        course.setDepartment(dto.getDepartment());
        course.setCourseType(dto.getCourseType() != null ? dto.getCourseType() : CourseType.THEORY);
        if (dto.getIsActive() != null) {
            course.setIsActive(dto.getIsActive());
        }
        return course;
    }

    public CourseDTO mapEntityToDto(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseCode(course.getCourseCode());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCredits());
        dto.setDepartment(course.getDepartment());
        dto.setCourseType(course.getCourseType());
        dto.setIsActive(course.getIsActive());
        return dto;
    }
}
