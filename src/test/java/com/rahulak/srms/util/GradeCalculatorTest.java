package com.rahulak.srms.util;

import com.rahulak.srms.util.GradeCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GradeCalculator}.
 *
 * <p>No Spring context needed — this is a pure utility class.
 * Tests cover every grade boundary (the most likely interview topic).
 */
class GradeCalculatorTest {

    // ── calculateGrade — boundary tests ──────────────────────────────────────

    @Test
    void testGrade_100_returns_O() {
        assertEquals("O", GradeCalculator.calculateGrade(100.0));
    }

    @Test
    void testGrade_90_returns_O() {
        assertEquals("O", GradeCalculator.calculateGrade(90.0));
    }

    @Test
    void testGrade_89_returns_Aplus() {
        // 89 is below the O threshold — must be A+
        assertEquals("A+", GradeCalculator.calculateGrade(89.0));
    }

    @Test
    void testGrade_80_returns_Aplus() {
        assertEquals("A+", GradeCalculator.calculateGrade(80.0));
    }

    @Test
    void testGrade_79_returns_A() {
        assertEquals("A", GradeCalculator.calculateGrade(79.0));
    }

    @Test
    void testGrade_70_returns_A() {
        assertEquals("A", GradeCalculator.calculateGrade(70.0));
    }

    @Test
    void testGrade_69_returns_Bplus() {
        assertEquals("B+", GradeCalculator.calculateGrade(69.0));
    }

    @Test
    void testGrade_60_returns_Bplus() {
        assertEquals("B+", GradeCalculator.calculateGrade(60.0));
    }

    @Test
    void testGrade_50_returns_B() {
        assertEquals("B", GradeCalculator.calculateGrade(50.0));
    }

    @Test
    void testGrade_45_returns_C() {
        assertEquals("C", GradeCalculator.calculateGrade(45.0));
    }

    @Test
    void testGrade_40_returns_P() {
        assertEquals("P", GradeCalculator.calculateGrade(40.0));
    }

    @Test
    void testGrade_39_returns_F() {
        // 39 is the critical boundary — must be F
        assertEquals("F", GradeCalculator.calculateGrade(39.0));
    }

    @Test
    void testGrade_0_returns_F() {
        assertEquals("F", GradeCalculator.calculateGrade(0.0));
    }

    // ── calculateGradePoint ───────────────────────────────────────────────────

    @Test
    void testGradePoint_90_returns_10() {
        assertEquals(10.0, GradeCalculator.calculateGradePoint(90.0), 0.001);
    }

    @Test
    void testGradePoint_39_returns_0() {
        assertEquals(0.0, GradeCalculator.calculateGradePoint(39.0), 0.001);
    }

    @Test
    void testGradePoint_40_returns_4() {
        assertEquals(4.0, GradeCalculator.calculateGradePoint(40.0), 0.001);
    }

    @Test
    void testGradePoint_80_returns_9() {
        assertEquals(9.0, GradeCalculator.calculateGradePoint(80.0), 0.001);
    }

    // ── isPassed ──────────────────────────────────────────────────────────────

    @Test
    void testIsPassed_40_returnsTrue() {
        assertTrue(GradeCalculator.isPassed(40.0));
    }

    @Test
    void testIsPassed_39_returnsFalse() {
        assertFalse(GradeCalculator.isPassed(39.0));
    }

    @Test
    void testIsPassed_100_returnsTrue() {
        assertTrue(GradeCalculator.isPassed(100.0));
    }

    @Test
    void testIsPassed_0_returnsFalse() {
        assertFalse(GradeCalculator.isPassed(0.0));
    }

    // ── Invalid input ─────────────────────────────────────────────────────────

    @Test
    void testInvalidMarks_negative_throwsException() {
        assertThrows(IllegalArgumentException.class,
            () -> GradeCalculator.calculateGrade(-1.0));
    }

    @Test
    void testInvalidMarks_over100_throwsException() {
        assertThrows(IllegalArgumentException.class,
            () -> GradeCalculator.calculateGrade(100.1));
    }

    @Test
    void testInvalidGradePoint_negative_throwsException() {
        assertThrows(IllegalArgumentException.class,
            () -> GradeCalculator.calculateGradePoint(-0.5));
    }
}
