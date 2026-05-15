package com.rahulak.srms.service;

import com.rahulak.srms.dto.CgpaDTO;
import com.rahulak.srms.dto.CourseResultDTO;
import com.rahulak.srms.dto.ResultDTO;
import com.rahulak.srms.dto.SemesterResultDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Grade;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for computing and assembling student result data.
 *
 * <p>CGPA aggregation uses raw {@link JdbcTemplate} (demonstrates JDBC JD requirement).
 * Semester-wise result assembly uses JPA repositories.
 */
@Service
@Transactional(readOnly = true)
public class ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultService.class);

    /** Raw JDBC query for CGPA — demonstrates 4-table JOIN and SQL aggregation. */
    private static final String CGPA_SQL = """
        SELECT
            COALESCE(SUM(c.credits * g.grade_point) / SUM(c.credits), 0) AS cgpa,
            COUNT(g.id) AS total_courses,
            SUM(CASE WHEN g.is_pass = FALSE THEN 1 ELSE 0 END) AS backlogs
        FROM students s
        JOIN enrollments e ON e.student_id = s.id
        JOIN courses     c ON c.id = e.course_id
        JOIN grades      g ON g.enrollment_id = e.id
        WHERE s.roll_number = ?
        """;

    private final JdbcTemplate jdbcTemplate;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ResultService(JdbcTemplate jdbcTemplate,
                         StudentRepository studentRepository,
                         EnrollmentRepository enrollmentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Computes CGPA for a student using raw JDBC.
     * Formula: SUM(grade_point * credits) / SUM(credits)
     */
    public CgpaDTO getCgpa(String rollNumber) {
        log.info("Calculating CGPA for student: {}", rollNumber);

        studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "roll number", rollNumber));

        CgpaDTO result = jdbcTemplate.queryForObject(CGPA_SQL, (rs, rowNum) -> {
            CgpaDTO dto = new CgpaDTO();
            dto.setCgpa(Math.round(rs.getDouble("cgpa") * 100.0) / 100.0);
            dto.setTotalCourses(rs.getInt("total_courses"));
            dto.setBacklogs(rs.getInt("backlogs"));
            return dto;
        }, rollNumber);

        log.info("CGPA for {}: {}", rollNumber, result != null ? result.getCgpa() : 0);
        return result != null ? result : new CgpaDTO(0.0, 0, 0);
    }

    /**
     * Assembles the full ResultDTO for a student — all semesters and courses.
     */
    public ResultDTO getFullResult(String rollNumber) {
        log.debug("getFullResult('{}')", rollNumber);

        Student student = studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "roll number", rollNumber));

        CgpaDTO cgpa = getCgpa(rollNumber);

        List<Enrollment> enrollments = enrollmentRepository
            .findByStudent_RollNumberOrderBySemesterAsc(rollNumber);

        // Group enrollments by semester
        Map<Integer, List<Enrollment>> bySemester = new LinkedHashMap<>();
        for (Enrollment e : enrollments) {
            bySemester.computeIfAbsent(e.getSemester(), k -> new ArrayList<>()).add(e);
        }

        List<SemesterResultDTO> semesters = new ArrayList<>();
        for (Map.Entry<Integer, List<Enrollment>> entry : bySemester.entrySet()) {
            semesters.add(buildSemesterResult(entry.getKey(), entry.getValue()));
        }

        ResultDTO result = new ResultDTO();
        result.setRollNumber(student.getRollNumber());
        result.setName(student.getName());
        result.setDepartment(student.getDepartment());
        result.setCgpa(cgpa.getCgpa());
        result.setTotalCourses(cgpa.getTotalCourses());
        result.setBacklogs(cgpa.getBacklogs());
        result.setSemesters(semesters);

        log.debug("getFullResult('{}') assembled {} semesters", rollNumber, semesters.size());
        return result;
    }

    /** Builds a SemesterResultDTO from a list of enrollments in that semester. */
    private SemesterResultDTO buildSemesterResult(Integer semester, List<Enrollment> enrollments) {
        SemesterResultDTO semResult = new SemesterResultDTO();
        semResult.setSemester(semester);

        List<CourseResultDTO> courses = new ArrayList<>();
        double weightedSum = 0;
        int totalCredits = 0;
        String academicYear = "";

        for (Enrollment e : enrollments) {
            academicYear = e.getAcademicYear();
            Grade grade = e.getGrade();
            if (grade != null) {
                int credits = e.getCourse().getCredits();
                totalCredits += credits;
                weightedSum += grade.getGradePoint() * credits;

                CourseResultDTO courseResult = new CourseResultDTO(
                    e.getCourse().getCourseCode(),
                    e.getCourse().getCourseName(),
                    credits,
                    grade.getMarksObtained(),
                    grade.getGrade(),
                    grade.getGradePoint(),
                    grade.getIsPass()
                );
                courses.add(courseResult);
            }
        }

        double gpa = (totalCredits > 0) ? Math.round(weightedSum / totalCredits * 100.0) / 100.0 : 0.0;
        semResult.setAcademicYear(academicYear);
        semResult.setGpa(gpa);
        semResult.setTotalCredits(totalCredits);
        semResult.setCourses(courses);
        return semResult;
    }

    /** Returns the result for a single semester. */
    public SemesterResultDTO getSemesterResult(String rollNumber, int semester) {
        log.debug("getSemesterResult('{}', {})", rollNumber, semester);

        studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "roll number", rollNumber));

        List<Enrollment> enrollments = enrollmentRepository
            .findByStudent_RollNumberAndSemester(rollNumber, semester);

        return buildSemesterResult(semester, enrollments);
    }
}
