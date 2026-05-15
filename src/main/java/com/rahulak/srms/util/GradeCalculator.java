package com.rahulak.srms.util;

/**
 * Pure static utility class for grade computation.
 *
 * <p>Contains no Spring dependencies — fully unit-testable without any context.
 *
 * <h3>Grade mapping table (per CUSAT pattern):</h3>
 * <pre>
 * Marks         Grade   Grade Point
 * 90 – 100      O       10.0   (Outstanding)
 * 80 – 89       A+       9.0   (Excellent)
 * 70 – 79       A        8.0   (Very Good)
 * 60 – 69       B+       7.0   (Good)
 * 50 – 59       B        6.0   (Above Average)
 * 45 – 49       C        5.0   (Average)
 * 40 – 44       P        4.0   (Pass)
 *  0 – 39       F        0.0   (Fail)
 * </pre>
 *
 * <p>Pass threshold: marks ≥ 40.
 */
public final class GradeCalculator {

    // Prevent instantiation
    private GradeCalculator() {
        throw new UnsupportedOperationException("GradeCalculator is a utility class");
    }

    /**
     * Calculates the letter grade for the given marks.
     *
     * @param marks marks obtained (0–100 inclusive)
     * @return letter grade string ("O", "A+", "A", "B+", "B", "C", "P", or "F")
     * @throws IllegalArgumentException if marks are outside 0–100
     */
    public static String calculateGrade(double marks) {
        validateMarks(marks);
        if (marks >= 90) return "O";
        if (marks >= 80) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 60) return "B+";
        if (marks >= 50) return "B";
        if (marks >= 45) return "C";
        if (marks >= 40) return "P";
        return "F";
    }

    /**
     * Calculates the numerical grade point for the given marks.
     *
     * @param marks marks obtained (0–100 inclusive)
     * @return grade point (10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, or 0.0)
     * @throws IllegalArgumentException if marks are outside 0–100
     */
    public static double calculateGradePoint(double marks) {
        validateMarks(marks);
        if (marks >= 90) return 10.0;
        if (marks >= 80) return 9.0;
        if (marks >= 70) return 8.0;
        if (marks >= 60) return 7.0;
        if (marks >= 50) return 6.0;
        if (marks >= 45) return 5.0;
        if (marks >= 40) return 4.0;
        return 0.0;
    }

    /**
     * Returns whether the student passed the course.
     *
     * @param marks marks obtained (0–100 inclusive)
     * @return {@code true} if marks ≥ 40
     * @throws IllegalArgumentException if marks are outside 0–100
     */
    public static boolean isPassed(double marks) {
        validateMarks(marks);
        return marks >= 40.0;
    }

    /**
     * Validates that marks are within the allowed range [0, 100].
     *
     * @throws IllegalArgumentException if marks are negative or exceed 100
     */
    private static void validateMarks(double marks) {
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException(
                "Marks must be between 0 and 100, but got: " + marks);
        }
    }
}
