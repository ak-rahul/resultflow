package com.rahulak.srms.controller.mvc;

import com.rahulak.srms.dto.CourseDTO;
import com.rahulak.srms.model.Course;
import com.rahulak.srms.model.CourseType;
import com.rahulak.srms.service.CourseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC controller for course management pages.
 *
 * <pre>
 * GET  /courses           → courses/list.html
 * GET  /courses/new       → courses/form.html
 * POST /courses/new       → save, redirect to /courses
 * GET  /courses/{id}/edit → courses/form.html (pre-filled)
 * POST /courses/{id}/edit → update, redirect to /courses
 * POST /courses/{id}/deactivate → deactivate, redirect to /courses
 * </pre>
 */
@Controller
@RequestMapping("/courses")
public class CourseMvcController {

    private static final Logger log = LoggerFactory.getLogger(CourseMvcController.class);

    private final CourseService courseService;

    public CourseMvcController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(@RequestParam(required = false) String department, Model model) {
        log.debug("GET /courses department='{}'", department);
        List<Course> courses = (department != null && !department.isBlank())
            ? courseService.findByDepartment(department)
            : courseService.findAll();
        model.addAttribute("courses", courses);
        model.addAttribute("selectedDepartment", department);
        model.addAttribute("courseTypes", CourseType.values());
        return "courses/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("courseDTO", new CourseDTO());
        model.addAttribute("courseTypes", CourseType.values());
        model.addAttribute("isEdit", false);
        return "courses/form";
    }

    @PostMapping("/new")
    public String saveCourse(@Valid @ModelAttribute("courseDTO") CourseDTO dto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("courseTypes", CourseType.values());
            model.addAttribute("isEdit", false);
            return "courses/form";
        }
        try {
            Course saved = courseService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Course '" + saved.getCourseName() + "' added successfully!");
            return "redirect:/courses";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("courseTypes", CourseType.values());
            model.addAttribute("isEdit", false);
            return "courses/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        CourseDTO dto = courseService.mapEntityToDto(course);
        model.addAttribute("courseDTO", dto);
        model.addAttribute("courseTypes", CourseType.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("courseId", id);
        return "courses/form";
    }

    @PostMapping("/{id}/edit")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute("courseDTO") CourseDTO dto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("courseTypes", CourseType.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("courseId", id);
            return "courses/form";
        }
        Course updated = courseService.update(id, dto);
        redirectAttributes.addFlashAttribute("successMessage",
            "Course '" + updated.getCourseName() + "' updated successfully!");
        return "redirect:/courses";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Course course = courseService.findById(id);
        courseService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMessage",
            "Course '" + course.getCourseName() + "' deactivated.");
        return "redirect:/courses";
    }
}
