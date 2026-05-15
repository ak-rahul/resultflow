package com.rahulak.srms.controller.mvc;

import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.service.CourseService;
import com.rahulak.srms.service.EnrollmentService;
import com.rahulak.srms.service.StudentService;
import com.rahulak.srms.exception.DuplicateEnrollmentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC controller for enrollment management.
 *
 * <pre>
 * GET  /enrollments/new → enrollment form (with optional studentId pre-filled)
 * POST /enrollments/new → create enrollment
 * </pre>
 */
@Controller
@RequestMapping("/enrollments")
public class EnrollmentMvcController {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentMvcController.class);

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentMvcController(EnrollmentService enrollmentService,
                                   StudentService studentService,
                                   CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping("/new")
    public String showEnrollForm(@RequestParam(required = false) Long studentId, Model model) {
        log.debug("GET /enrollments/new studentId={}", studentId);
        List<Student> students = studentService.findAllActive();
        List<Course> courses = courseService.findAllActive();
        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("preselectedStudentId", studentId);
        return "enrollments/form";
    }

    @PostMapping("/new")
    public String enroll(@RequestParam Long studentId,
                         @RequestParam Long courseId,
                         @RequestParam Integer semester,
                         @RequestParam String academicYear,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        log.debug("POST /enrollments/new studentId={} courseId={} sem={} year={}",
            studentId, courseId, semester, academicYear);
        try {
            Enrollment enrollment = enrollmentService.enroll(studentId, courseId, semester, academicYear);
            redirectAttributes.addFlashAttribute("successMessage",
                "Enrollment successful! " + enrollment.getStudent().getName()
                    + " enrolled in " + enrollment.getCourse().getCourseName());
            return "redirect:/students/" + studentId;
        } catch (DuplicateEnrollmentException e) {
            List<Student> students = studentService.findAllActive();
            List<Course> courses = courseService.findAllActive();
            model.addAttribute("students", students);
            model.addAttribute("courses", courses);
            model.addAttribute("preselectedStudentId", studentId);
            model.addAttribute("errorMessage", e.getMessage());
            return "enrollments/form";
        }
    }
}
