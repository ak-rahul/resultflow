/**
 * srms.js — Custom JavaScript for SRMS
 *
 * Features:
 *  1. Live grade preview on the grade entry page
 *  2. Marks input validation highlight
 *  3. Auto-dismiss alerts after 5 seconds
 */

'use strict';

// ── Grade calculation (mirrors GradeCalculator.java exactly) ──────────────

/**
 * Compute the letter grade for a given marks value.
 * @param {number} marks - 0 to 100
 * @returns {{ grade: string, gradePoint: number, isPassed: boolean }}
 */
function computeGrade(marks) {
    if (marks < 0 || marks > 100) return { grade: '—', gradePoint: '—', isPassed: false };
    if (marks >= 90) return { grade: 'O',  gradePoint: 10.0, isPassed: true };
    if (marks >= 80) return { grade: 'A+', gradePoint: 9.0,  isPassed: true };
    if (marks >= 70) return { grade: 'A',  gradePoint: 8.0,  isPassed: true };
    if (marks >= 60) return { grade: 'B+', gradePoint: 7.0,  isPassed: true };
    if (marks >= 50) return { grade: 'B',  gradePoint: 6.0,  isPassed: true };
    if (marks >= 45) return { grade: 'C',  gradePoint: 5.0,  isPassed: true };
    if (marks >= 40) return { grade: 'P',  gradePoint: 4.0,  isPassed: true };
    return { grade: 'F', gradePoint: 0.0, isPassed: false };
}

// ── Live Grade Preview ────────────────────────────────────────────────────

/**
 * Called on every `input` event on a marks field.
 * Updates the grade preview span and grade point span in the same table row.
 *
 * @param {HTMLInputElement} input - the marks input element
 */
function updateGradePreview(input) {
    const value = parseFloat(input.value);
    const enrollmentId = input.id.replace('marks_', '');

    const gradeSpan = document.getElementById('grade_' + enrollmentId);
    const gpSpan    = document.getElementById('gp_'    + enrollmentId);

    if (isNaN(value) || input.value.trim() === '') {
        if (gradeSpan) { gradeSpan.textContent = '—'; gradeSpan.className = 'badge srms-grade-badge'; }
        if (gpSpan)    { gpSpan.textContent = '—'; }
        input.classList.remove('invalid');
        return;
    }

    if (value < 0 || value > 100) {
        input.classList.add('invalid');
        if (gradeSpan) { gradeSpan.textContent = '!'; gradeSpan.className = 'badge srms-grade-badge srms-grade-f'; }
        if (gpSpan)    { gpSpan.textContent = '—'; }
        return;
    }

    input.classList.remove('invalid');
    const result = computeGrade(value);

    if (gradeSpan) {
        gradeSpan.textContent = result.grade;
        // Pick colour class
        let cls = 'srms-grade-badge ';
        if (result.grade === 'O' || result.grade === 'A+') cls += 'srms-grade-o';
        else if (result.grade === 'F')                      cls += 'srms-grade-f';
        else                                                cls += 'srms-grade-pass';
        gradeSpan.className = 'badge ' + cls;
    }
    if (gpSpan) {
        gpSpan.textContent = result.gradePoint.toFixed(1);
    }
}

// ── Auto-dismiss Alerts ──────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss success alerts after 5 seconds
    const successAlerts = document.querySelectorAll('.srms-alert-success');
    successAlerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // Highlight active nav link based on current URL
    const currentPath = window.location.pathname;
    document.querySelectorAll('.srms-nav-link').forEach(function (link) {
        const href = link.getAttribute('href');
        if (href && href !== '/' && currentPath.startsWith(href)) {
            link.classList.add('active');
        }
    });
});
