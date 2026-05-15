package com.rahulak.srms.service;

import com.rahulak.srms.repository.CourseRepository;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.GradeRepository;
import com.rahulak.srms.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service providing aggregate statistics for the dashboard page.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public DashboardService(StudentRepository studentRepository,
                            CourseRepository courseRepository,
                            EnrollmentRepository enrollmentRepository,
                            GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.gradeRepository = gradeRepository;
    }

    public long totalStudents() { return studentRepository.count(); }
    public long activeStudents() { return studentRepository.findByIsActiveTrue().size(); }
    public long totalCourses()  { return courseRepository.count(); }
    public long totalEnrollments() { return enrollmentRepository.count(); }
    public long gradesEntered() { return gradeRepository.count(); }
}
