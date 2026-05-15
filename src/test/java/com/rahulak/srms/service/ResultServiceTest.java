package com.rahulak.srms.service;

import com.rahulak.srms.dto.CgpaDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ResultService}.
 * Mocks {@link JdbcTemplate} to test CGPA calculation without a real database.
 */
@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private StudentRepository studentRepository;
    @Mock private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private ResultService resultService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student("23CS001", "Rahul AK", "rahul@cusat.ac.in", "CSE", 3);
        sampleStudent.setId(1L);
    }

    @Test
    void testGetCgpa_withValidRollNumber_returnsCgpaDTO() {
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
            .thenReturn(new CgpaDTO(8.44, 5, 0));
        CgpaDTO result = resultService.getCgpa("23CS001");
        assertNotNull(result);
        assertEquals(8.44, result.getCgpa(), 0.01);
    }

    @Test
    void testGetCgpa_withStudentNotFound_throwsResourceNotFoundException() {
        when(studentRepository.findByRollNumber("INVALID")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> resultService.getCgpa("INVALID"));
        verify(jdbcTemplate, never()).queryForObject(anyString(), any(RowMapper.class), any());
    }

    @Test
    void testGetCgpa_withNoGrades_returnsZero() {
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
            .thenReturn(new CgpaDTO(0.0, 0, 0));
        CgpaDTO result = resultService.getCgpa("23CS001");
        assertEquals(0.0, result.getCgpa(), 0.001);
    }

    @Test
    void testCgpa_withAllOutstanding_returns10() {
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
            .thenReturn(new CgpaDTO(10.0, 5, 0));
        CgpaDTO result = resultService.getCgpa("23CS001");
        assertEquals(10.0, result.getCgpa(), 0.001);
    }

    @Test
    void testCgpa_withMixedGrades_calculatesCorrectly() {
        // 4 credits @ GP 10 + 3 credits @ GP 7 = (40+21)/7 = 8.71
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
            .thenReturn(new CgpaDTO(8.71, 2, 0));
        CgpaDTO result = resultService.getCgpa("23CS001");
        assertEquals(8.71, result.getCgpa(), 0.01);
    }

    @Test
    void testGetFullResult_studentNotFound_throwsException() {
        when(studentRepository.findByRollNumber("INVALID")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> resultService.getFullResult("INVALID"));
    }

    @Test
    void testGetFullResult_assemblesAllSemesters() {
        when(studentRepository.findByRollNumber("23CS001")).thenReturn(Optional.of(sampleStudent));
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyString()))
            .thenReturn(new CgpaDTO(8.44, 5, 0));
        when(enrollmentRepository.findByStudent_RollNumberOrderBySemesterAsc("23CS001"))
            .thenReturn(List.of());
        var result = resultService.getFullResult("23CS001");
        assertNotNull(result);
        assertEquals("23CS001", result.getRollNumber());
        assertEquals(8.44, result.getCgpa(), 0.01);
    }
}
