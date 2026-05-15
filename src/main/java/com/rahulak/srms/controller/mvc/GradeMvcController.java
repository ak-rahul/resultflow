package com.rahulak.srms.controller.mvc;

import com.rahulak.srms.dto.GradeEntryDTO;
import com.rahulak.srms.model.Enrollment;
import com.rahulak.srms.model.Student;
import com.rahulak.srms.service.GradeService;
import com.rahulak.srms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MVC controller for grade entry.
 *
 * <pre>
 * GET  /grades/entry                → show grade entry form (step 1: enter roll number)
 * GET  /grades/entry?studentId=X    → show courses for student (step 2)
 * POST /grades/entry                → batch save all grades
 * POST /grades/{id}/edit            → update a single grade
 * </pre>
 */
@Controller
@RequestMapping("/grades")
public class GradeMvcController {

    private static final Logger log = LoggerFactory.getLogger(GradeMvcController.class);

    private final GradeService gradeService;
    private final StudentService studentService;

    public GradeMvcController(GradeService gradeService, StudentService studentService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
    }

    @GetMapping("/entry")
    public String showGradeEntry(@RequestParam(required = false) Long studentId, Model model) {
        log.debug("GET /grades/entry studentId={}", studentId);

        List<Student> students = studentService.findAllActive();
        model.addAttribute("students", students);

        if (studentId != null) {
            Student student = studentService.findById(studentId);
            List<Enrollment> ungradedEnrollments = gradeService.findUngradedEnrollments(studentId);
            model.addAttribute("selectedStudent", student);
            model.addAttribute("enrollments", ungradedEnrollments);
        }
        return "grades/entry";
    }

    @PostMapping("/entry")
    public String submitGrades(@RequestParam Long studentId,
                               @RequestParam Map<String, String> allParams,
                               RedirectAttributes redirectAttributes) {
        log.debug("POST /grades/entry studentId={}", studentId);

        List<GradeEntryDTO> dtos = new ArrayList<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("marks_")) {
                try {
                    Long enrollmentId = Long.parseLong(entry.getKey().replace("marks_", ""));
                    double marks = Double.parseDouble(entry.getValue());
                    dtos.add(new GradeEntryDTO(enrollmentId, marks));
                } catch (NumberFormatException ignored) {
                    // skip non-numeric values
                }
            }
        }

        int savedCount = 0;
        for (GradeEntryDTO dto : dtos) {
            gradeService.enterGrade(dto);
            savedCount++;
        }

        redirectAttributes.addFlashAttribute("successMessage",
            savedCount + " grade(s) saved successfully!");
        return "redirect:/students/" + studentId;
    }

    @PostMapping("/{id}/edit")
    public String editGrade(@PathVariable Long id,
                            @RequestParam double marks,
                            @RequestParam Long studentId,
                            RedirectAttributes redirectAttributes) {
        log.debug("POST /grades/{}/edit marks={}", id, marks);
        gradeService.updateGrade(id, marks);
        redirectAttributes.addFlashAttribute("successMessage", "Grade updated successfully!");
        return "redirect:/students/" + studentId;
    }
}
