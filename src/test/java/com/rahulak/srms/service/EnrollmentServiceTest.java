package com.rahulak.srms.service;

import com.rahulak.srms.exception.DuplicateEnrollmentException;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.CourseRepository;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EnrollmentService}.
 * Mocks {@link EnrollmentRepository}, {@link StudentRepository}, and {@link CourseRepository}.
 */
@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = new Student("23CS001", "Rahul AK", "rahul@cusat.ac.in", "CSE", 3);
        student.setId(1L);

        course = new Course();
        course.setId(1L);
        course.setCourseCode("CS301");
        course.setCourseName("Data Structures");
        course.setCredits(4);
        course.setDepartment("CSE");
    }

    // ── enroll — success ──────────────────────────────────────────────────────

    @Test
    void testEnroll_validData_savesEnrollment() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndAcademicYear(
                1L, 1L, 3, "2024-25")).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> {
            Enrollment e = (Enrollment) i.getArguments()[0];
            e.setId(100L);
            return e;
        });

        Enrollment result = enrollmentService.enroll(1L, 1L, 3, "2024-25");

        assertNotNull(result);
        assertEquals(3, result.getSemester());
        assertEquals("2024-25", result.getAcademicYear());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    // ── enroll — duplicate prevention ────────────────────────────────────────

    @Test
    void testEnroll_duplicateEnrollment_throwsDuplicateEnrollmentException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndAcademicYear(
                1L, 1L, 3, "2024-25")).thenReturn(true);

        assertThrows(DuplicateEnrollmentException.class,
            () -> enrollmentService.enroll(1L, 1L, 3, "2024-25"));

        // Repository save must never be called
        verify(enrollmentRepository, never()).save(any());
    }

    // ── enroll — student not found ────────────────────────────────────────────

    @Test
    void testEnroll_studentNotFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> enrollmentService.enroll(999L, 1L, 3, "2024-25"));

        verify(courseRepository, never()).findById(any());
        verify(enrollmentRepository, never()).save(any());
    }

    // ── enroll — course not found ─────────────────────────────────────────────

    @Test
    void testEnroll_courseNotFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> enrollmentService.enroll(1L, 999L, 3, "2024-25"));

        verify(enrollmentRepository, never()).save(any());
    }
}
