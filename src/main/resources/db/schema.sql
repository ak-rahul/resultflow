-- ─────────────────────────────────────────────────────────
-- SRMS Database Schema
-- Run once to create all tables in srms_db
-- ─────────────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS srms_db;
USE srms_db;

-- ─────────────────────────────────────────────
-- Table 1: students
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS students (
    id            BIGINT          AUTO_INCREMENT PRIMARY KEY,
    roll_number   VARCHAR(20)     NOT NULL UNIQUE,
    name          VARCHAR(100)    NOT NULL,
    email         VARCHAR(100)    UNIQUE,
    phone         VARCHAR(15),
    department    VARCHAR(100)    NOT NULL,
    current_sem   INT             NOT NULL DEFAULT 1,
    date_of_birth DATE,
    is_active     BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT chk_semester CHECK (current_sem BETWEEN 1 AND 10)
);

-- ─────────────────────────────────────────────
-- Table 2: courses
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    id            BIGINT          AUTO_INCREMENT PRIMARY KEY,
    course_code   VARCHAR(20)     NOT NULL UNIQUE,
    course_name   VARCHAR(150)    NOT NULL,
    credits       INT             NOT NULL,
    department    VARCHAR(100)    NOT NULL,
    course_type   ENUM('THEORY','LAB','PROJECT') DEFAULT 'THEORY',
    is_active     BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_credits CHECK (credits BETWEEN 1 AND 6)
);

-- ─────────────────────────────────────────────
-- Table 3: enrollments
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS enrollments (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT      NOT NULL,
    course_id       BIGINT      NOT NULL,
    semester        INT         NOT NULL,
    academic_year   VARCHAR(10) NOT NULL,
    enrolled_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,

    -- Prevent duplicate enrollment
    UNIQUE KEY uq_enrollment (student_id, course_id, semester, academic_year)
);

-- ─────────────────────────────────────────────
-- Table 4: grades
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS grades (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    enrollment_id   BIGINT          NOT NULL UNIQUE,
    marks_obtained  DECIMAL(5,2)    NOT NULL,
    grade           VARCHAR(5),
    grade_point     DECIMAL(4,2),
    is_pass         BOOLEAN,
    entered_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,

    CONSTRAINT chk_marks CHECK (marks_obtained BETWEEN 0 AND 100)
);
