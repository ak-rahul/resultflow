package com.rahulak.srms.service;

import com.rahulak.srms.exception.DuplicateEnrollmentException;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.CourseRepository;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing student-course enrollments.
 *
 * <p>Key business rule: a student cannot be enrolled in the same course
 * more than once for the same semester and academic year
 * ({@link DuplicateEnrollmentException} is thrown if attempted).
 */
@Service
@Transactional
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudentId(Long studentId) {
        log.debug("findByStudentId({})", studentId);
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public Enrollment findById(Long id) {
        log.debug("findById({})", id);
        return enrollmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));
    }

    // ── Enroll ────────────────────────────────────────────────────────────────

    /**
     * Enrolls a student in a course for a given semester and academic year.
     *
     * <p>Validation steps:
     * <ol>
     *   <li>Student must exist</li>
     *   <li>Course must exist</li>
     *   <li>Duplicate must not exist</li>
     * </ol>
     *
     * @throws ResourceNotFoundException     if student or course not found
     * @throws DuplicateEnrollmentException if enrollment already exists
     */
    public Enrollment enroll(Long studentId, Long courseId, Integer semester, String academicYear) {
        log.debug("enroll() — studentId={}, courseId={}, semester={}, year={}",
            studentId, courseId, semester, academicYear);

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        boolean alreadyEnrolled = enrollmentRepository
            .existsByStudentIdAndCourseIdAndSemesterAndAcademicYear(
                studentId, courseId, semester, academicYear);

        if (alreadyEnrolled) {
            log.warn("Duplicate enrollment: student={}, course={}, sem={}, year={}",
                student.getRollNumber(), course.getCourseCode(), semester, academicYear);
            throw new DuplicateEnrollmentException(
                student.getRollNumber(), course.getCourseCode(), semester, academicYear);
        }

        Enrollment enrollment = new Enrollment(student, course, semester, academicYear);
        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment saved: id={}, student={}, course={}",
            saved.getId(), student.getRollNumber(), course.getCourseCode());
        return saved;
    }

    /** Deletes an enrollment (and cascades to the associated grade if any). */
    public void delete(Long enrollmentId) {
        log.debug("delete() — enrollmentId={}", enrollmentId);
        Enrollment enrollment = findById(enrollmentId);
        enrollmentRepository.delete(enrollment);
        log.info("Enrollment deleted: id={}", enrollmentId);
    }
}
