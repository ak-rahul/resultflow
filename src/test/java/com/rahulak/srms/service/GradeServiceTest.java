package com.rahulak.srms.service;

import com.rahulak.srms.dto.GradeEntryDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Grade;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.GradeRepository;
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
 * Unit tests for {@link GradeService}.
 * Mocks both {@link GradeRepository} and {@link EnrollmentRepository}.
 */
@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock private GradeRepository gradeRepository;
    @Mock private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private GradeService gradeService;

    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        Student student = new Student("23CS001", "Rahul AK", "rahul@cusat.ac.in", "CSE", 3);
        student.setId(1L);

        Course course = new Course();
        course.setId(1L);
        course.setCourseCode("CS301");
        course.setCourseName("Data Structures");
        course.setCredits(4);

        enrollment = new Enrollment(student, course, 3, "2024-25");
        enrollment.setId(1L);
    }

    // ── enterGrade — valid cases ──────────────────────────────────────────────

    @Test
    void testEnterGrade_marks91_savesGradeO() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        GradeEntryDTO dto = new GradeEntryDTO(1L, 91.0);
        Grade saved = gradeService.enterGrade(dto);

        assertEquals("O",   saved.getGrade());
        assertEquals(10.0,  saved.getGradePoint(), 0.001);
        assertTrue(saved.getIsPass());
        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void testEnterGrade_marks39_savesGradeF_and_isPassFalse() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        GradeEntryDTO dto = new GradeEntryDTO(1L, 39.0);
        Grade saved = gradeService.enterGrade(dto);

        assertEquals("F",  saved.getGrade());
        assertEquals(0.0,  saved.getGradePoint(), 0.001);
        assertFalse(saved.getIsPass());
    }

    @Test
    void testEnterGrade_marks40_savesGradeP_and_isPassTrue() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(1L)).thenReturn(Optional.empty());
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        GradeEntryDTO dto = new GradeEntryDTO(1L, 40.0);
        Grade saved = gradeService.enterGrade(dto);

        assertEquals("P", saved.getGrade());
        assertTrue(saved.getIsPass());
    }

    // ── enterGrade — invalid enrollment ───────────────────────────────────────

    @Test
    void testEnterGrade_invalidEnrollment_throwsResourceNotFoundException() {
        when(enrollmentRepository.findById(999L)).thenReturn(Optional.empty());

        GradeEntryDTO dto = new GradeEntryDTO(999L, 75.0);
        assertThrows(ResourceNotFoundException.class, () -> gradeService.enterGrade(dto));
        verify(gradeRepository, never()).save(any());
    }

    // ── enterGrade — upsert (update existing) ─────────────────────────────────

    @Test
    void testEnterGrade_duplicateEntry_updatesExisting() {
        Grade existing = new Grade();
        existing.setId(10L);
        existing.setMarksObtained(70.0);
        existing.setGrade("A");

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.findByEnrollmentId(1L)).thenReturn(Optional.of(existing));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        GradeEntryDTO dto = new GradeEntryDTO(1L, 85.0);
        Grade saved = gradeService.enterGrade(dto);

        assertEquals("A+", saved.getGrade());
        assertEquals(9.0,  saved.getGradePoint(), 0.001);
        assertTrue(saved.getIsPass());
        // Same grade record updated, not a new one
        assertEquals(10L, saved.getId());
    }

    // ── updateGrade ───────────────────────────────────────────────────────────

    @Test
    void testUpdateGrade_recalculatesGradeAndPoint() {
        Grade grade = new Grade();
        grade.setId(5L);
        grade.setMarksObtained(50.0);
        grade.setGrade("B");
        grade.setGradePoint(6.0);

        when(gradeRepository.findById(5L)).thenReturn(Optional.of(grade));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        Grade updated = gradeService.updateGrade(5L, 92.0);
        assertEquals("O",  updated.getGrade());
        assertEquals(10.0, updated.getGradePoint(), 0.001);
        assertTrue(updated.getIsPass());
    }
}
