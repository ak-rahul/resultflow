package com.rahulak.srms.repository;

import com.rahulak.srms.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Course}.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /** Find a course by its unique code (e.g. "CS301"). */
    Optional<Course> findByCourseCode(String courseCode);

    /** Find all courses for a department. */
    List<Course> findByDepartment(String department);

    /** Find all active courses. */
    List<Course> findByIsActiveTrue();

    /** Find all active courses for a department. */
    List<Course> findByDepartmentAndIsActiveTrue(String department);

    /** Check if a course code is already taken. */
    boolean existsByCourseCode(String courseCode);
}
