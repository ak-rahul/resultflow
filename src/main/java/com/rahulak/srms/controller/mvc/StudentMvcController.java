package com.rahulak.srms.controller.mvc;

import com.rahulak.srms.dto.StudentDTO;
import com.rahulak.srms.dto.ResultDTO;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.service.ResultService;
import com.rahulak.srms.service.StudentService;
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
 * MVC controller for student management pages.
 *
 * <pre>
 * GET  /students             → students/list.html
 * GET  /students/new         → students/form.html (add)
 * POST /students/new         → save, redirect to /students
 * GET  /students/{id}        → students/detail.html
 * GET  /students/{id}/edit   → students/form.html (edit)
 * POST /students/{id}/edit   → update, redirect to /students/{id}
 * POST /students/{id}/deactivate → deactivate, redirect to /students
 * </pre>
 */
@Controller
@RequestMapping("/students")
public class StudentMvcController {

    private static final Logger log = LoggerFactory.getLogger(StudentMvcController.class);

    private final StudentService studentService;
    private final ResultService resultService;

    public StudentMvcController(StudentService studentService, ResultService resultService) {
        this.studentService = studentService;
        this.resultService = resultService;
    }

    @GetMapping
    public String listStudents(@RequestParam(required = false) String query, Model model) {
        log.debug("GET /students query='{}'", query);
        List<Student> students = (query != null && !query.isBlank())
            ? studentService.search(query)
            : studentService.findAll();
        model.addAttribute("students", students);
        model.addAttribute("query", query);
        return "students/list";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        log.debug("GET /students/new");
        model.addAttribute("studentDTO", new StudentDTO());
        model.addAttribute("isEdit", false);
        return "students/form";
    }

    @PostMapping("/new")
    public String saveStudent(@Valid @ModelAttribute("studentDTO") StudentDTO dto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        log.debug("POST /students/new");
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "students/form";
        }
        try {
            Student saved = studentService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                "Student '" + saved.getName() + "' added successfully!");
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            return "students/form";
        }
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        log.debug("GET /students/{}", id);
        Student student = studentService.findById(id);
        ResultDTO result = resultService.getFullResult(student.getRollNumber());
        model.addAttribute("student", student);
        model.addAttribute("result", result);
        return "students/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        log.debug("GET /students/{}/edit", id);
        Student student = studentService.findById(id);
        StudentDTO dto = studentService.mapEntityToDto(student);
        model.addAttribute("studentDTO", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("studentId", id);
        return "students/form";
    }

    @PostMapping("/{id}/edit")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("studentDTO") StudentDTO dto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.debug("POST /students/{}/edit", id);
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("studentId", id);
            return "students/form";
        }
        Student updated = studentService.update(id, dto);
        redirectAttributes.addFlashAttribute("successMessage",
            "Student '" + updated.getName() + "' updated successfully!");
        return "redirect:/students/" + id;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("POST /students/{}/deactivate", id);
        Student student = studentService.findById(id);
        studentService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMessage",
            "Student '" + student.getName() + "' has been deactivated.");
        return "redirect:/students";
    }
}
