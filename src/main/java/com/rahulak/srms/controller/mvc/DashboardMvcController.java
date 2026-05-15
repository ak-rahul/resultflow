package com.rahulak.srms.controller.mvc;

import com.rahulak.srms.model.Student;
import com.rahulak.srms.service.DashboardService;
import com.rahulak.srms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * MVC controller for the dashboard (index) page.
 * Handles {@code GET /} and {@code GET /dashboard}.
 */
@Controller
public class DashboardMvcController {

    private static final Logger log = LoggerFactory.getLogger(DashboardMvcController.class);

    private final DashboardService dashboardService;
    private final StudentService studentService;

    public DashboardMvcController(DashboardService dashboardService, StudentService studentService) {
        this.dashboardService = dashboardService;
        this.studentService = studentService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        log.debug("GET / (dashboard)");

        model.addAttribute("totalStudents",    dashboardService.totalStudents());
        model.addAttribute("activeStudents",   dashboardService.activeStudents());
        model.addAttribute("totalCourses",     dashboardService.totalCourses());
        model.addAttribute("totalEnrollments", dashboardService.totalEnrollments());
        model.addAttribute("gradesEntered",    dashboardService.gradesEntered());

        List<Student> recentStudents = studentService.findAll();
        model.addAttribute("recentStudents", recentStudents.subList(0, Math.min(10, recentStudents.size())));

        return "index";
    }
}
