package com.rahulak.srms.controller.rest;

import com.rahulak.srms.dto.CgpaDTO;
import com.rahulak.srms.dto.ResultDTO;
import com.rahulak.srms.dto.SemesterResultDTO;
import com.rahulak.srms.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for student result and CGPA endpoints.
 *
 * <pre>
 * GET /api/results/{rollNo}             → full ResultDTO (CGPA + all semesters)
 * GET /api/results/{rollNo}/cgpa        → CgpaDTO (from raw JDBC)
 * GET /api/results/{rollNo}/sem/{sem}   → SemesterResultDTO for one semester
 * </pre>
 */
@RestController
@RequestMapping("/api/results")
public class ResultRestController {

    private static final Logger log = LoggerFactory.getLogger(ResultRestController.class);

    private final ResultService resultService;

    public ResultRestController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping("/{rollNumber}")
    public ResponseEntity<ResultDTO> getFullResult(@PathVariable String rollNumber) {
        log.debug("GET /api/results/{}", rollNumber);
        ResultDTO result = resultService.getFullResult(rollNumber);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{rollNumber}/cgpa")
    public ResponseEntity<CgpaDTO> getCgpa(@PathVariable String rollNumber) {
        log.debug("GET /api/results/{}/cgpa", rollNumber);
        CgpaDTO cgpa = resultService.getCgpa(rollNumber);
        return ResponseEntity.ok(cgpa);
    }

    @GetMapping("/{rollNumber}/sem/{semester}")
    public ResponseEntity<SemesterResultDTO> getSemesterResult(
            @PathVariable String rollNumber,
            @PathVariable int semester) {
        log.debug("GET /api/results/{}/sem/{}", rollNumber, semester);
        SemesterResultDTO result = resultService.getSemesterResult(rollNumber, semester);
        return ResponseEntity.ok(result);
    }
}
