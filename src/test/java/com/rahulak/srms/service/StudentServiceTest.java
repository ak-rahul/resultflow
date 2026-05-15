package com.rahulak.srms.service;

import com.rahulak.srms.dto.StudentDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StudentService}.
 * Uses Mockito to mock {@link StudentRepository} — no database required.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student("23CS001", "Rahul AK", "rahul@cusat.ac.in", "CSE", 3);
        sampleStudent.setId(1L);
        sampleStudent.setIsActive(true);
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void testFindAll_returnsAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(sampleStudent));
        List<Student> result = studentService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    // ── findByRollNumber ──────────────────────────────────────────────────────

    @Test
    void testFindByRollNumber_found_returnsStudent() {
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        Student result = studentService.findByRollNumber("23CS001");
        assertNotNull(result);
        assertEquals("23CS001", result.getRollNumber());
        assertEquals("Rahul AK", result.getName());
    }

    @Test
    void testFindByRollNumber_notFound_throwsResourceNotFoundException() {
        when(studentRepository.findByRollNumber("99XX999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
            () -> studentService.findByRollNumber("99XX999"));
    }

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    void testSave_withValidData_callsRepositorySave() {
        when(studentRepository.existsByRollNumber("23CS002")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        StudentDTO dto = new StudentDTO();
        dto.setRollNumber("23CS002");
        dto.setName("Priya Nair");
        dto.setDepartment("CSE");
        dto.setCurrentSem(3);

        Student saved = studentService.save(dto);
        assertNotNull(saved);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testSave_duplicateRollNumber_throwsIllegalArgumentException() {
        when(studentRepository.existsByRollNumber("23CS001")).thenReturn(true);

        StudentDTO dto = new StudentDTO();
        dto.setRollNumber("23CS001");
        dto.setName("Duplicate");
        dto.setDepartment("CSE");
        dto.setCurrentSem(1);

        assertThrows(IllegalArgumentException.class, () -> studentService.save(dto));
        verify(studentRepository, never()).save(any());
    }

    // ── deactivate ────────────────────────────────────────────────────────────

    @Test
    void testDeactivate_setsIsActiveFalse() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        studentService.deactivate(1L);

        assertFalse(sampleStudent.getIsActive());
        verify(studentRepository, times(1)).save(sampleStudent);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void testFindById_notFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> studentService.findById(999L));
    }
}
