package com.rahulak.srms.service;

import com.rahulak.srms.dto.GradeEntryDTO;
import com.rahulak.srms.exception.ResourceNotFoundException;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Grade;
import com.rahulak.srms.repository.EnrollmentRepository;
import com.rahulak.srms.repository.GradeRepository;
import com.rahulak.srms.util.GradeCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for grade entry and update operations.
 * Delegates all grade/point computation to {@link GradeCalculator}.
 */
@Service
@Transactional
public class GradeService {

    private static final Logger log = LoggerFactory.getLogger(GradeService.class);

    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;

    public GradeService(GradeRepository gradeRepository, EnrollmentRepository enrollmentRepository) {
        this.gradeRepository = gradeRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public Grade findById(Long id) {
        log.debug("findById({})", id);
        return gradeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));
    }

    @Transactional(readOnly = true)
    public Optional<Grade> findByEnrollmentId(Long enrollmentId) {
        return gradeRepository.findByEnrollmentId(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Grade> findByRollNumber(String rollNumber) {
        return gradeRepository.findByEnrollment_Student_RollNumber(rollNumber);
    }

    /**
     * Enters or updates a grade. If a grade already exists for the enrollment
     * it is updated (upsert behaviour). Grade fields are computed by GradeCalculator.
     */
    public Grade enterGrade(GradeEntryDTO dto) {
        log.debug("enterGrade() enrollmentId={}, marks={}", dto.getEnrollmentId(), dto.getMarksObtained());

        Enrollment enrollment = enrollmentRepository.findById(dto.getEnrollmentId())
            .orElseThrow(() -> {
                log.warn("Enrollment not found: id={}", dto.getEnrollmentId());
                return new ResourceNotFoundException("Enrollment", "id", dto.getEnrollmentId());
            });

        double marks = dto.getMarksObtained();
        Grade grade = gradeRepository.findByEnrollmentId(dto.getEnrollmentId()).orElse(new Grade());
        grade.setEnrollment(enrollment);
        grade.setMarksObtained(marks);
        grade.setGrade(GradeCalculator.calculateGrade(marks));
        grade.setGradePoint(GradeCalculator.calculateGradePoint(marks));
        grade.setIsPass(GradeCalculator.isPassed(marks));

        Grade saved = gradeRepository.save(grade);
        log.info("Grade saved: enrollmentId={}, marks={}, grade={}, isPass={}",
            dto.getEnrollmentId(), marks, saved.getGrade(), saved.getIsPass());
        return saved;
    }

    /** Updates marks for an existing grade, recalculating all computed fields. */
    public Grade updateGrade(Long gradeId, double newMarks) {
        log.debug("updateGrade() gradeId={}, newMarks={}", gradeId, newMarks);
        Grade grade = findById(gradeId);
        grade.setMarksObtained(newMarks);
        grade.setGrade(GradeCalculator.calculateGrade(newMarks));
        grade.setGradePoint(GradeCalculator.calculateGradePoint(newMarks));
        grade.setIsPass(GradeCalculator.isPassed(newMarks));
        Grade updated = gradeRepository.save(grade);
        log.info("Grade updated: gradeId={}, grade={}", gradeId, updated.getGrade());
        return updated;
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findUngradedEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
            .filter(e -> e.getGrade() == null)
            .toList();
    }
}
