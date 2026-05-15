# Student Result Management System (SRMS)
## Comprehensive MVP Action Plan

> **Role target:** Cognizant DN 5.0 — Programmer Analyst / Programmer Analyst Trainee (Java FSE)
> **JD coverage:** Java · Spring MVC · Spring Boot · Spring REST · MySQL · JDBC · HTML5 · CSS3 · Bootstrap · JUnit · Mockito · Logging

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Feature Decisions — What's In, What's Out](#2-feature-decisions)
3. [System Architecture](#3-system-architecture)
4. [Database Design](#4-database-design)
5. [Complete File & Package Structure](#5-file--package-structure)
6. [Layer-by-Layer Implementation Plan](#6-layer-by-layer-implementation-plan)
7. [REST API Contract](#7-rest-api-contract)
8. [Frontend Pages Plan](#8-frontend-pages-plan)
9. [Unit Testing Plan (JUnit + Mockito)](#9-unit-testing-plan)
10. [Logging Strategy](#10-logging-strategy)
11. [Configuration Files](#11-configuration-files)
12. [Build Order & Weekly Action Plan](#12-build-order--weekly-action-plan)
13. [Sample Code Blueprints](#13-sample-code-blueprints)
14. [Interview Talking Points](#14-interview-talking-points)

---

## 1. Project Overview

### What is SRMS?

A web-based academic result management system where:

- **Admins** can manage students, courses, enrollments, and enter/update grades
- **Viewers** can look up any student's results, CGPA, and semester-wise transcripts

### Why This Project for the JD?

| JD Requirement | How SRMS Covers It |
|---|---|
| Java language, coding standards | Entire backend in Java 17 |
| HTML5, CSS3, Bootstrap | Thymeleaf-rendered frontend |
| MySQL CRUD + table relationships | 4 related tables with FK constraints |
| JDBC | Raw JDBC for CGPA aggregation query |
| Spring MVC presentation layer | Admin panel via Thymeleaf + Spring MVC |
| Spring REST + Spring Boot | JSON API endpoints alongside MVC views |
| JUnit + Mockito unit tests | 15+ tests across 4 service classes |
| Logging + code coverage | SLF4J/Logback + JaCoCo |
| Data structures + algorithms | Grade mapping, CGPA weighted average |
| Fix defects with minimum support | Global exception handler |

### Tech Stack Decision

| Layer | Choice | Why |
|---|---|---|
| Language | Java 17 | LTS, modern features |
| Framework | Spring Boot 3.2 | Auto-config, embedded Tomcat |
| View layer | Thymeleaf | Native Spring MVC integration |
| ORM | Spring Data JPA (Hibernate) | For standard CRUD |
| Direct DB | Spring JDBC Template | For CGPA aggregation (covers JDBC JD req) |
| Database | MySQL 8.0 | JD explicitly requires MySQL |
| Frontend | Bootstrap 5 + Thymeleaf | JD requires Bootstrap |
| Build | Maven | Industry standard |
| Testing | JUnit 5 + Mockito | Exactly what the JD mentions |
| Logging | SLF4J + Logback | Spring Boot default |
| Coverage | JaCoCo | Maven plugin, shows coverage % |

---

## 2. Feature Decisions

### What's IN the MVP (Justified)

Every feature below maps to at least one JD requirement.

#### Student Management
- Add, edit, view, and deactivate students
- Fields: roll number, name, email, department, semester, date of birth
- List with search by name or roll number
- Student detail page showing all results

#### Course Management
- Add, edit, view courses
- Fields: course code, name, credits, department, semester
- List all courses with filter by department

#### Enrollment Management
- Enroll a student into a course for a specific semester/year
- Prevent duplicate enrollment (same student + course + semester)
- View all enrollments per student

#### Grade Management
- Enter marks for an enrolled student
- Auto-calculate: grade (O/A+/A/B+/B/C/P/F) and grade point (10/9/8/7/6/5/4/0)
- Edit existing grades
- Validation: marks must be 0–100

#### Result & CGPA
- Per-student result view: all semesters, all courses, marks, grade, grade point, credits
- CGPA calculation: `Σ(grade_point × credits) / Σ(credits)` — computed via **raw JDBC**
- Semester-wise GPA breakdown
- Pass/fail status per course (pass ≥ 40 marks)
- Backlogs count (failed courses)

#### REST API
- Full JSON API parallel to the MVC views (demonstrates both patterns)
- Student CRUD, result fetch, transcript endpoint

#### Logging
- Entry/exit logging in service methods
- Error logging in exception handler
- CGPA calculation logged with input/output

#### Unit Tests
- `ResultServiceTest` — CGPA logic
- `GradeServiceTest` — grade mapping, boundary cases
- `StudentServiceTest` — CRUD with mocked repo
- `EnrollmentServiceTest` — duplicate prevention logic

### What's OUT of the MVP (and Why)

| Feature | Reason Out |
|---|---|
| Login / Authentication | Spring Security adds 2–3 days; JD doesn't mention it; explain this in interview as "next iteration" |
| Student self-login portal | Same reason — scope creep for MVP |
| PDF transcript download | iText library complexity; not in JD |
| Email notifications | Not in JD; over-engineering for MVP |
| Pagination on all tables | Nice to have; add only if time permits |
| Docker / deployment | Not required for campus interviews |
| React/Angular frontend | JD says Bootstrap; don't over-engineer |

---

## 3. System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser (Client)                         │
│           Thymeleaf pages (Bootstrap 5 + Vanilla JS)            │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP
            ┌────────────────┴─────────────────┐
            │           Spring Boot App          │
            │         (Embedded Tomcat)          │
            │                                    │
            │  ┌──────────────────────────────┐  │
            │  │        Controllers            │  │
            │  │  MVC Controllers (Thymeleaf)  │  │
            │  │  REST Controllers (JSON API)  │  │
            │  └──────────────┬───────────────┘  │
            │                 │                   │
            │  ┌──────────────▼───────────────┐  │
            │  │         Service Layer         │  │
            │  │  StudentService               │  │
            │  │  CourseService                │  │
            │  │  EnrollmentService            │  │
            │  │  GradeService                 │  │
            │  │  ResultService  ◄── CGPA calc │  │
            │  └──────────────┬───────────────┘  │
            │                 │                   │
            │  ┌──────────────▼───────────────┐  │
            │  │       Repository Layer        │  │
            │  │  JPA Repositories (CRUD)      │  │
            │  │  JdbcTemplate (CGPA query)    │  │
            │  └──────────────┬───────────────┘  │
            │                 │                   │
            └─────────────────┼───────────────────┘
                              │ JDBC
            ┌─────────────────▼───────────────────┐
            │           MySQL 8.0 Database         │
            │  students · courses · enrollments    │
            │  grades                              │
            └─────────────────────────────────────┘
```

### Request Flow — MVC (Admin Panel)

```
Browser → GET /students
       → StudentMvcController.listStudents()
       → StudentService.findAll()
       → StudentRepository.findAll()  [JPA]
       → MySQL
       → List<Student> returned
       → Model populated
       → Thymeleaf renders students/list.html
       → HTML response to browser
```

### Request Flow — REST API

```
Client → GET /api/students/23CS001/results
       → ResultRestController.getStudentResults()
       → ResultService.getFullResult("23CS001")
       → JdbcTemplate.query(CGPA_SQL)  [raw JDBC]
       → MySQL
       → ResultDTO assembled
       → Jackson serializes to JSON
       → 200 OK { student, cgpa, semesters: [...] }
```

---

## 4. Database Design

### Entity Relationship

```
students ──< enrollments >── courses
                 │
                 └──< grades
```

### DDL Scripts

```sql
-- database setup
CREATE DATABASE IF NOT EXISTS srms_db;
USE srms_db;

-- ─────────────────────────────────────────────
-- Table 1: students
-- ─────────────────────────────────────────────
CREATE TABLE students (
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
CREATE TABLE courses (
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
CREATE TABLE enrollments (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT      NOT NULL,
    course_id       BIGINT      NOT NULL,
    semester        INT         NOT NULL,
    academic_year   VARCHAR(10) NOT NULL,   -- e.g. "2024-25"
    enrolled_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,
    
    -- Prevent duplicate enrollment
    UNIQUE KEY uq_enrollment (student_id, course_id, semester, academic_year)
);

-- ─────────────────────────────────────────────
-- Table 4: grades
-- ─────────────────────────────────────────────
CREATE TABLE grades (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    enrollment_id   BIGINT          NOT NULL UNIQUE,   -- one grade per enrollment
    marks_obtained  DECIMAL(5,2)    NOT NULL,
    grade           VARCHAR(5),     -- O, A+, A, B+, B, C, P, F — computed by app
    grade_point     DECIMAL(3,2),   -- 10, 9, 8, 7, 6, 5, 4, 0 — computed by app
    is_pass         BOOLEAN,        -- marks >= 40
    entered_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_marks CHECK (marks_obtained BETWEEN 0 AND 100)
);
```

### Seed Data (for testing)

```sql
-- Sample departments: CSE, ECE, EEE, MECH, CIVIL

INSERT INTO students (roll_number, name, email, department, current_sem) VALUES
('23CS001', 'Rahul AK',         'rahul@cusat.ac.in',  'CSE', 3),
('23CS002', 'Priya Nair',       'priya@cusat.ac.in',  'CSE', 3),
('23EC001', 'Arjun Menon',      'arjun@cusat.ac.in',  'ECE', 3);

INSERT INTO courses (course_code, course_name, credits, department) VALUES
('CS301', 'Data Structures',          4, 'CSE'),
('CS302', 'Database Management',      3, 'CSE'),
('CS303', 'Operating Systems',        4, 'CSE'),
('CS304', 'Computer Networks',        3, 'CSE'),
('CS305', 'DS Lab',                   2, 'CSE');

INSERT INTO enrollments (student_id, course_id, semester, academic_year) VALUES
(1, 1, 3, '2024-25'), (1, 2, 3, '2024-25'), (1, 3, 3, '2024-25'),
(1, 4, 3, '2024-25'), (1, 5, 3, '2024-25');

INSERT INTO grades (enrollment_id, marks_obtained, grade, grade_point, is_pass) VALUES
(1, 91, 'O',  10.0, TRUE),
(2, 82, 'A+',  9.0, TRUE),
(3, 74, 'B+',  7.0, TRUE),
(4, 65, 'B',   6.0, TRUE),
(5, 88, 'O',  10.0, TRUE);
```

### Grade Mapping Table

| Marks Range | Grade | Grade Point |
|---|---|---|
| 90 – 100 | O (Outstanding) | 10 |
| 80 – 89 | A+ (Excellent) | 9 |
| 70 – 79 | A (Very Good) | 8 |
| 60 – 69 | B+ (Good) | 7 |
| 50 – 59 | B (Above Average) | 6 |
| 45 – 49 | C (Average) | 5 |
| 40 – 44 | P (Pass) | 4 |
| 0 – 39 | F (Fail) | 0 |

### CGPA Calculation (Raw JDBC Query)

```sql
SELECT 
    SUM(c.credits * g.grade_point) / SUM(c.credits) AS cgpa,
    COUNT(g.id)                                      AS total_courses,
    SUM(CASE WHEN g.is_pass = FALSE THEN 1 ELSE 0 END) AS backlogs
FROM students s
JOIN enrollments e ON e.student_id = s.id
JOIN courses     c ON c.id = e.course_id
JOIN grades      g ON g.enrollment_id = e.id
WHERE s.roll_number = ?;
```

---

## 5. File & Package Structure

### Maven Project Root

```
srms/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── yourname/
│   │   │           └── srms/
│   │   │               ├── SrmsApplication.java
│   │   │               │
│   │   │               ├── config/
│   │   │               │   └── WebConfig.java
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── mvc/
│   │   │               │   │   ├── StudentMvcController.java
│   │   │               │   │   ├── CourseMvcController.java
│   │   │               │   │   ├── EnrollmentMvcController.java
│   │   │               │   │   └── GradeMvcController.java
│   │   │               │   └── rest/
│   │   │               │       ├── StudentRestController.java
│   │   │               │       ├── CourseRestController.java
│   │   │               │       └── ResultRestController.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── StudentService.java
│   │   │               │   ├── CourseService.java
│   │   │               │   ├── EnrollmentService.java
│   │   │               │   ├── GradeService.java
│   │   │               │   └── ResultService.java
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   ├── StudentRepository.java
│   │   │               │   ├── CourseRepository.java
│   │   │               │   ├── EnrollmentRepository.java
│   │   │               │   └── GradeRepository.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Student.java
│   │   │               │   ├── Course.java
│   │   │               │   ├── Enrollment.java
│   │   │               │   └── Grade.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── StudentDTO.java
│   │   │               │   ├── CourseDTO.java
│   │   │               │   ├── GradeEntryDTO.java
│   │   │               │   ├── ResultDTO.java
│   │   │               │   ├── SemesterResultDTO.java
│   │   │               │   └── CgpaDTO.java
│   │   │               │
│   │   │               ├── exception/
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── DuplicateEnrollmentException.java
│   │   │               │   ├── InvalidMarksException.java
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               │
│   │   │               └── util/
│   │   │                   └── GradeCalculator.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── db/
│   │       │   ├── schema.sql
│   │       │   └── data.sql
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── srms.css
│   │       │   └── js/
│   │       │       └── srms.js
│   │       └── templates/
│   │           ├── layout/
│   │           │   └── base.html           (Thymeleaf layout)
│   │           ├── index.html              (Dashboard)
│   │           ├── students/
│   │           │   ├── list.html
│   │           │   ├── form.html
│   │           │   └── detail.html
│   │           ├── courses/
│   │           │   ├── list.html
│   │           │   └── form.html
│   │           ├── enrollments/
│   │           │   └── form.html
│   │           └── grades/
│   │               └── entry.html
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── yourname/
│                   └── srms/
│                       ├── service/
│                       │   ├── StudentServiceTest.java
│                       │   ├── GradeServiceTest.java
│                       │   ├── ResultServiceTest.java
│                       │   └── EnrollmentServiceTest.java
│                       └── util/
│                           └── GradeCalculatorTest.java
│
├── pom.xml
└── README.md
```

### Why This Structure?

- `controller/mvc/` and `controller/rest/` are split to keep responsibilities clear — great talking point in interviews
- `dto/` layer separates API contracts from DB entities — shows you know layered architecture
- `util/GradeCalculator` is isolated so it's trivially unit-testable
- `exception/` package shows production-quality error handling

---

## 6. Layer-by-Layer Implementation Plan

### Layer 1: Model (Entities)

**Student.java** — annotate with `@Entity`, `@Table(name="students")`. Fields: id, rollNumber, name, email, phone, department, currentSem, dateOfBirth, isActive, createdAt, updatedAt. Add `@OneToMany(mappedBy="student")` to link enrollments.

**Course.java** — `@Entity`, fields: id, courseCode, courseName, credits, department, courseType (use `@Enumerated(EnumType.STRING)`), isActive.

**Enrollment.java** — `@Entity`, `@ManyToOne` to Student and Course. Fields: id, student, course, semester, academicYear, enrolledAt. Add `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {...}))` to enforce DB-level uniqueness.

**Grade.java** — `@Entity`, `@OneToOne` to Enrollment. Fields: id, enrollment, marksObtained, grade, gradePoint, isPass, enteredAt, updatedAt.

### Layer 2: Repository

All extend `JpaRepository<Entity, Long>`.

**StudentRepository** — add:
```java
Optional<Student> findByRollNumber(String rollNumber);
List<Student> findByDepartment(String department);
List<Student> findByNameContainingIgnoreCase(String name);
boolean existsByRollNumber(String rollNumber);
```

**CourseRepository** — add:
```java
Optional<Course> findByCourseCode(String code);
List<Course> findByDepartment(String department);
```

**EnrollmentRepository** — add:
```java
List<Enrollment> findByStudentIdAndSemester(Long studentId, int semester);
boolean existsByStudentIdAndCourseIdAndSemesterAndAcademicYear(...);
```

**GradeRepository** — add:
```java
Optional<Grade> findByEnrollmentId(Long enrollmentId);
List<Grade> findByEnrollment_Student_RollNumber(String rollNumber);
```

### Layer 3: Utility

**GradeCalculator.java** — pure static utility, no Spring dependencies. This is the key unit-testable class.

```
calculateGrade(double marks)  →  "O" / "A+" / "A" / ... / "F"
calculateGradePoint(double marks)  →  10.0 / 9.0 / ... / 0.0
isPassed(double marks)  →  marks >= 40
```

Use a chain of if-else-if with exact boundary checks. This is where boundary unit tests shine.

### Layer 4: Service

**StudentService** — CRUD wrapper over StudentRepository. Key methods:
- `findAll()` — returns all active students
- `findByRollNumber(String)` — throws `ResourceNotFoundException` if absent
- `save(StudentDTO)` — maps DTO → entity, saves
- `deactivate(Long)` — sets isActive=false (never hard-delete)
- `search(String query)` — delegates to repo findByNameContaining

**CourseService** — similar CRUD pattern.

**EnrollmentService** — key logic:
- `enroll(Long studentId, Long courseId, int semester, String year)`:
  1. Check if student exists
  2. Check if course exists
  3. Check duplicate: if exists → throw `DuplicateEnrollmentException`
  4. Save enrollment

**GradeService** — key logic:
- `enterGrade(GradeEntryDTO dto)`:
  1. Find enrollment
  2. Call `GradeCalculator.calculateGrade(marks)` and `calculateGradePoint(marks)`
  3. Build Grade entity, save
- `updateGrade(Long gradeId, double newMarks)` — recalculates grade/point

**ResultService** — most important service:
- `getCgpa(String rollNumber)` — **uses JdbcTemplate directly** to run the CGPA aggregation SQL
- `getFullResult(String rollNumber)` — assembles complete ResultDTO with all semesters
- `getSemesterResult(String rollNumber, int semester)` — single semester view

### Layer 5: Controllers

#### MVC Controllers (Thymeleaf)

**StudentMvcController** — `@Controller`, base mapping `/students`:
```
GET  /students          → list.html (all students)
GET  /students/new      → form.html (empty form)
POST /students/new      → save, redirect to /students
GET  /students/{id}     → detail.html (with results)
GET  /students/{id}/edit → form.html (pre-filled)
POST /students/{id}/edit → update, redirect
POST /students/{id}/deactivate → deactivate, redirect
```

**CourseMvcController** — similar CRUD pattern at `/courses`.

**EnrollmentMvcController** — `GET/POST /enrollments/new`:
- GET: show form with student dropdown + course dropdown
- POST: call EnrollmentService.enroll(), handle DuplicateEnrollmentException

**GradeMvcController** — `GET/POST /grades/entry`:
- GET: show grade entry form (select student → load courses)
- POST: call GradeService.enterGrade()
- `POST /grades/{id}/edit` — update grade

#### REST Controllers

**StudentRestController** — `@RestController`, base `/api/students`:
```
GET    /api/students                  → List<StudentDTO>
POST   /api/students                  → create, return 201
GET    /api/students/{rollNo}         → StudentDTO
PUT    /api/students/{rollNo}         → update
DELETE /api/students/{rollNo}         → deactivate (200 OK)
```

**ResultRestController** — `@RestController`, base `/api/results`:
```
GET /api/results/{rollNo}             → full ResultDTO (CGPA + all sems)
GET /api/results/{rollNo}/cgpa        → CgpaDTO {cgpa, totalCourses, backlogs}
GET /api/results/{rollNo}/sem/{sem}   → SemesterResultDTO
```

**CourseRestController** — `@RestController`, base `/api/courses`:
```
GET  /api/courses           → all courses
POST /api/courses           → create
GET  /api/courses/{code}    → single course
```

### Layer 6: Exception Handling

**GlobalExceptionHandler.java** — `@ControllerAdvice`:

```
ResourceNotFoundException     → 404 (REST) / error page (MVC)
DuplicateEnrollmentException  → 409 (REST) / error msg on form (MVC)
InvalidMarksException         → 400 (REST) / form validation error (MVC)
MethodArgumentNotValidException → 400 with field errors
Exception (catch-all)         → 500 with logged stack trace
```

For MVC: redirect to a shared `error.html` with the message in the model.
For REST: return `ErrorResponse` DTO `{ timestamp, status, message, path }`.

---

## 7. REST API Contract

### Response Format

**Success (single resource)**
```json
{
  "rollNumber": "23CS001",
  "name": "Rahul AK",
  "department": "CSE",
  "currentSem": 3,
  "email": "rahul@cusat.ac.in"
}
```

**Error Response**
```json
{
  "timestamp": "2025-05-15T10:30:00",
  "status": 404,
  "message": "Student with roll number 23CS999 not found",
  "path": "/api/students/23CS999"
}
```

**Full Result Response**
```json
{
  "rollNumber": "23CS001",
  "name": "Rahul AK",
  "cgpa": 8.44,
  "totalCourses": 5,
  "backlogs": 0,
  "semesters": [
    {
      "semester": 3,
      "academicYear": "2024-25",
      "gpa": 8.44,
      "totalCredits": 16,
      "courses": [
        {
          "courseCode": "CS301",
          "courseName": "Data Structures",
          "credits": 4,
          "marksObtained": 91.0,
          "grade": "O",
          "gradePoint": 10.0,
          "isPassed": true
        }
      ]
    }
  ]
}
```

### HTTP Status Codes Used

| Scenario | Code |
|---|---|
| Successful GET | 200 OK |
| Resource created | 201 Created |
| Successful deactivate | 200 OK |
| Validation failed | 400 Bad Request |
| Resource not found | 404 Not Found |
| Duplicate enrollment | 409 Conflict |
| Server error | 500 Internal Server Error |

---

## 8. Frontend Pages Plan

### Page 1: Dashboard (`/` → `index.html`)

**Layout:** 4 stat cards (Total Students, Total Courses, Grades Entered, Active Enrollments) + Recent students table.

**Tech:** Bootstrap grid, cards. Counts fetched from service in controller, passed to model.

**Bootstrap components used:** `.card`, `.table`, `.badge`

### Page 2: Student List (`/students` → `students/list.html`)

**Features:**
- Table: Roll No | Name | Department | Semester | Status | Actions
- Search bar (GET form with `?query=` param)
- "Add Student" button (→ /students/new)
- Action buttons: View, Edit, Deactivate
- Bootstrap badges for status (Active = green, Inactive = red)

### Page 3: Add/Edit Student (`/students/new`, `/students/{id}/edit` → `students/form.html`)

**Features:**
- Form fields: Roll Number, Name, Email, Phone, Department (select), Current Semester (select 1–10), Date of Birth
- Bootstrap form validation (HTML5 `required`, `type="email"`, `min`/`max`)
- Thymeleaf form binding with `th:object` and `th:field`
- Error messages rendered from `BindingResult`
- Submit → POST → redirect with success message (Flash attribute)

### Page 4: Student Detail (`/students/{id}` → `students/detail.html`)

**The main page — most visually complex.**

**Features:**
- Student info card at the top (name, roll, dept, sem, CGPA badge)
- CGPA displayed prominently (large badge, colour-coded: ≥8.5 = green, ≥6.5 = yellow, <6.5 = red)
- Backlogs count
- Accordion (Bootstrap) for each semester
  - Each semester panel: GPA for that semester + table of courses (code, name, credits, marks, grade, grade point, pass/fail badge)
- "Enroll in Course" button → /enrollments/new?studentId=X
- "Enter Grades" button → /grades/entry?studentId=X

### Page 5: Course List (`/courses` → `courses/list.html`)

Simple table: Code | Name | Credits | Department | Type | Actions. Filter dropdown by department.

### Page 6: Add Course (`/courses/new` → `courses/form.html`)

Form with: Course Code, Name, Credits (number input), Department, Type (radio: Theory/Lab/Project).

### Page 7: Enrollment Form (`/enrollments/new` → `enrollments/form.html`)

**Two-step form (single page):**
1. Select Student (dropdown with search or text input with roll number)
2. Select Course, Semester (1–10), Academic Year (text "2024-25")
3. Submit → shows success or duplicate error message

### Page 8: Grade Entry (`/grades/entry` → `grades/entry.html`)

**Features:**
- Step 1: Enter roll number → show student info + list of enrolled courses without grades
- Step 2: For each course, enter marks (0–100 number input)
- Live preview: JavaScript calculates and shows predicted grade as user types
- Submit all grades at once (batch entry)
- Validation: marks out of range highlighted red

### Shared Layout (`templates/layout/base.html`)

Using Thymeleaf Layout Dialect (or simple `th:replace`):
- Navbar: SRMS logo, nav links (Dashboard, Students, Courses, Grades)
- Sidebar (optional for desktop)
- Flash message bar (success/error from redirects)
- Footer

### CSS / JS Plan (`srms.css`, `srms.js`)

`srms.css` — override Bootstrap for brand colour (pick a blue/teal), custom CGPA badge colours, table hover, card shadow.

`srms.js` — one function: live grade preview on grade entry page. Listen to `input` event on marks field, compute grade, update a `<span>` next to the field. Pure vanilla JS, no jQuery.

---

## 9. Unit Testing Plan

### Philosophy

Write tests that have a **clear assertion and a meaningful name**. Recruiters look at your test names to understand how you think about edge cases.

### Test 1: `GradeCalculatorTest.java`

Tests for the pure utility class — no mocks needed, no Spring context.

```
testGrade_90_returns_O()
testGrade_89_returns_Aplus()
testGrade_80_returns_Aplus()
testGrade_79_returns_A()
testGrade_40_returns_P()
testGrade_39_returns_F()
testGrade_0_returns_F()
testGrade_100_returns_O()
testGradePoint_90_returns_10()
testGradePoint_39_returns_0()
testIsPassed_40_returnsTrue()
testIsPassed_39_returnsFalse()
testInvalidMarks_negative_throwsException()
testInvalidMarks_over100_throwsException()
```

These 14 tests cover every boundary. The 89/90 and 39/40 boundaries are the ones interviewers ask about.

### Test 2: `ResultServiceTest.java`

Uses Mockito to mock `JdbcTemplate`.

```
testGetCgpa_withValidRollNumber_returnsCgpaDTO()
testGetCgpa_withNoGrades_returnsZero()
testGetCgpa_withStudentNotFound_throwsException()
testCgpa_withAllOutstanding_returns10()
testCgpa_withMixedGrades_calculatesCorrectly()
testGetFullResult_assemblesAllSemesters()
```

Key assertion for CGPA: given 4 credits (grade point 10) and 3 credits (grade point 7), CGPA = (40 + 21) / 7 = 8.71.

### Test 3: `GradeServiceTest.java`

Mocks `GradeRepository` and `EnrollmentRepository`.

```
testEnterGrade_savesCorrectGradeAndPoint()
testEnterGrade_invalidEnrollment_throwsException()
testUpdateGrade_recalculatesGradeAndPoint()
testEnterGrade_marks91_savesGradeO()
testEnterGrade_marks39_savesGradeF_and_isPassFalse()
testEnterGrade_duplicateEntry_updatesExisting()
```

### Test 4: `StudentServiceTest.java`

Mocks `StudentRepository`.

```
testFindAll_returnsAllActiveStudents()
testFindByRollNumber_found_returnsStudent()
testFindByRollNumber_notFound_throwsResourceNotFoundException()
testSave_withValidData_callsRepositorySave()
testDeactivate_setsIsActiveFalse()
testSave_duplicateRollNumber_throwsException()
```

### Test 5: `EnrollmentServiceTest.java`

Mocks `EnrollmentRepository`, `StudentRepository`, `CourseRepository`.

```
testEnroll_validData_savesEnrollment()
testEnroll_duplicateEnrollment_throwsDuplicateEnrollmentException()
testEnroll_studentNotFound_throwsResourceNotFoundException()
testEnroll_courseNotFound_throwsResourceNotFoundException()
```

### Running Tests and Coverage

```bash
# Run all tests
mvn test

# Run with JaCoCo coverage report
mvn test jacoco:report

# Coverage report at: target/site/jacoco/index.html
# Target: >80% line coverage on service and util layers
```

Add to `pom.xml` for coverage:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

---

## 10. Logging Strategy

### Setup

Spring Boot auto-configures SLF4J + Logback. No extra dependency needed.

In every class:
```java
private static final Logger log = LoggerFactory.getLogger(ClassName.class);
```

### What to Log

| Location | Level | What |
|---|---|---|
| Service method entry | DEBUG | Method name + key params |
| Service method exit | DEBUG | Return value summary |
| ResourceNotFoundException | WARN | Roll number / ID not found |
| DuplicateEnrollmentException | WARN | Student + course + semester |
| CGPA calculation | INFO | Roll number + calculated CGPA |
| Grade saved | INFO | Roll number + course + grade |
| GlobalExceptionHandler catch-all | ERROR | Full stack trace |

### `application.properties` Logging Config

```properties
# Log level by package
logging.level.com.yourname.srms=DEBUG
logging.level.org.springframework=WARN
logging.level.org.hibernate.SQL=DEBUG

# Log to file
logging.file.name=logs/srms.log
logging.pattern.console=%d{HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} %-5level [%thread] %logger{36} - %msg%n
```

---

## 11. Configuration Files

### `pom.xml` (complete dependencies)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>

    <groupId>com.yourname</groupId>
    <artifactId>srms</artifactId>
    <version>1.0.0</version>
    <name>Student Result Management System</name>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web (Spring MVC + Embedded Tomcat) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Thymeleaf (MVC view layer) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <!-- Spring Data JPA (ORM + CRUD repositories) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Spring JDBC (JdbcTemplate for CGPA query) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!-- Validation (Bean Validation for DTOs) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- DevTools (hot reload during development) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <!-- Includes: JUnit 5, Mockito, AssertJ, Spring Test -->
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!-- JaCoCo for code coverage -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution><goals><goal>prepare-agent</goal></goals></execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals><goal>report</goal></goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### `application.properties`

```properties
# ── Database ──────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/srms_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ── JPA / Hibernate ───────────────────────────────
spring.jpa.hibernate.ddl-auto=validate
# Use 'create' on first run to create tables, then switch to 'validate'
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# ── Thymeleaf ─────────────────────────────────────
spring.thymeleaf.cache=false

# ── Logging ───────────────────────────────────────
logging.level.com.yourname.srms=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.file.name=logs/srms.log

# ── Server ────────────────────────────────────────
server.port=8080
spring.application.name=srms
```

---

## 12. Build Order & Weekly Action Plan

### Week 1 — Foundation (Days 1–7)

**Day 1: Project setup**
- [ ] Create Spring Boot project via [start.spring.io](https://start.spring.io) with dependencies: Web, Thymeleaf, Data JPA, JDBC, Validation, MySQL Driver, DevTools
- [ ] Set up MySQL database, run `schema.sql`
- [ ] Configure `application.properties`
- [ ] Verify app starts: `mvn spring-boot:run`

**Day 2: Entities + Repositories**
- [ ] Write all 4 entity classes (Student, Course, Enrollment, Grade)
- [ ] Write all 4 JPA repositories with custom query methods
- [ ] Set `spring.jpa.hibernate.ddl-auto=create` temporarily to verify schema
- [ ] Run seed data SQL

**Day 3: GradeCalculator utility + tests**
- [ ] Write `GradeCalculator.java` (pure static methods)
- [ ] Write `GradeCalculatorTest.java` (all 14 tests)
- [ ] Run: `mvn test -pl . -Dtest=GradeCalculatorTest`
- [ ] All 14 tests should pass

**Day 4: Service layer (Student + Course)**
- [ ] Write `StudentService` and `CourseService`
- [ ] Write `ResourceNotFoundException`, `DuplicateEnrollmentException`
- [ ] Write `StudentServiceTest` (6 tests with Mockito)

**Day 5: Service layer (Enrollment + Grade + Result)**
- [ ] Write `EnrollmentService` with duplicate check
- [ ] Write `GradeService` using `GradeCalculator`
- [ ] Write `ResultService` with `JdbcTemplate` for CGPA
- [ ] Write `ResultServiceTest` and `GradeServiceTest`

**Day 6: REST Controllers**
- [ ] Write `StudentRestController`, `CourseRestController`, `ResultRestController`
- [ ] Write `GlobalExceptionHandler` for REST responses
- [ ] Test all endpoints with Postman or browser

**Day 7: Buffer / fix issues**
- [ ] Fix any bugs found during REST testing
- [ ] Verify all tests still pass: `mvn test`

---

### Week 2 — Frontend (Days 8–14)

**Day 8: Shared layout + Dashboard**
- [ ] Create `layout/base.html` (navbar, footer, flash messages)
- [ ] Create `index.html` (dashboard with stat counts)
- [ ] Write `DashboardMvcController`

**Day 9: Student list + add/edit forms**
- [ ] `students/list.html` with search
- [ ] `students/form.html` (add + edit, Thymeleaf form binding)
- [ ] `StudentMvcController` (GET/POST for list, new, edit, deactivate)

**Day 10: Student detail page (results view)**
- [ ] `students/detail.html` with CGPA badge + accordion per semester
- [ ] Wire to `ResultService.getFullResult()`
- [ ] CGPA colour coding with Thymeleaf conditional classes

**Day 11: Course management pages**
- [ ] `courses/list.html` and `courses/form.html`
- [ ] `CourseMvcController`

**Day 12: Enrollment + Grade entry pages**
- [ ] `enrollments/form.html`
- [ ] `grades/entry.html` with live grade preview JS
- [ ] `EnrollmentMvcController`, `GradeMvcController`

**Day 13: Polish + error pages**
- [ ] Create `error.html` for MVC error display
- [ ] Add Bootstrap alerts for success/error flash messages
- [ ] Write custom `srms.css` (brand colour, CGPA badge colours)

**Day 14: Final testing + README**
- [ ] Full end-to-end test: add student → enroll → enter grades → view results
- [ ] Verify all unit tests pass: `mvn test`
- [ ] Generate coverage report: `mvn test jacoco:report`
- [ ] Write `README.md` (setup instructions, feature list, screenshots)

---

## 13. Sample Code Blueprints

### GradeCalculator.java

```java
package com.yourname.srms.util;

public class GradeCalculator {

    public static String calculateGrade(double marks) {
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }
        if (marks >= 90) return "O";
        if (marks >= 80) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 60) return "B+";
        if (marks >= 50) return "B";
        if (marks >= 45) return "C";
        if (marks >= 40) return "P";
        return "F";
    }

    public static double calculateGradePoint(double marks) {
        if (marks >= 90) return 10.0;
        if (marks >= 80) return 9.0;
        if (marks >= 70) return 8.0;
        if (marks >= 60) return 7.0;
        if (marks >= 50) return 6.0;
        if (marks >= 45) return 5.0;
        if (marks >= 40) return 4.0;
        return 0.0;
    }

    public static boolean isPassed(double marks) {
        return marks >= 40.0;
    }
}
```

### ResultService.java (JDBC section)

```java
@Service
public class ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultService.class);

    private final JdbcTemplate jdbcTemplate;
    private final StudentRepository studentRepository;

    private static final String CGPA_SQL = """
        SELECT
            SUM(c.credits * g.grade_point) / SUM(c.credits) AS cgpa,
            COUNT(g.id) AS total_courses,
            SUM(CASE WHEN g.is_pass = FALSE THEN 1 ELSE 0 END) AS backlogs
        FROM students s
        JOIN enrollments e ON e.student_id = s.id
        JOIN courses     c ON c.id = e.course_id
        JOIN grades      g ON g.enrollment_id = e.id
        WHERE s.roll_number = ?
        """;

    public ResultService(JdbcTemplate jdbcTemplate,
                         StudentRepository studentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.studentRepository = studentRepository;
    }

    public CgpaDTO getCgpa(String rollNumber) {
        log.info("Calculating CGPA for student: {}", rollNumber);

        studentRepository.findByRollNumber(rollNumber)
            .orElseThrow(() ->
                new ResourceNotFoundException("Student not found: " + rollNumber));

        CgpaDTO result = jdbcTemplate.queryForObject(CGPA_SQL, (rs, rowNum) -> {
            CgpaDTO dto = new CgpaDTO();
            dto.setCgpa(rs.getDouble("cgpa"));
            dto.setTotalCourses(rs.getInt("total_courses"));
            dto.setBacklogs(rs.getInt("backlogs"));
            return dto;
        }, rollNumber);

        log.info("CGPA for {}: {}", rollNumber, result != null ? result.getCgpa() : 0);
        return result;
    }
}
```

### GradeServiceTest.java (Mockito example)

```java
@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private GradeService gradeService;

    @Test
    void testEnterGrade_marks91_savesGradeO() {
        // Arrange
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> i.getArguments()[0]);

        GradeEntryDTO dto = new GradeEntryDTO();
        dto.setEnrollmentId(1L);
        dto.setMarksObtained(91.0);

        // Act
        Grade saved = gradeService.enterGrade(dto);

        // Assert
        assertEquals("O", saved.getGrade());
        assertEquals(10.0, saved.getGradePoint());
        assertTrue(saved.getIsPass());
        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void testEnterGrade_invalidEnrollment_throwsException() {
        when(enrollmentRepository.findById(999L)).thenReturn(Optional.empty());

        GradeEntryDTO dto = new GradeEntryDTO();
        dto.setEnrollmentId(999L);
        dto.setMarksObtained(75.0);

        assertThrows(ResourceNotFoundException.class, () -> gradeService.enterGrade(dto));
    }
}
```

### GlobalExceptionHandler.java

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── REST handlers ────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex,
                                        HttpServletRequest request) {
        return new ErrorResponse(404, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateEnrollmentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(DuplicateEnrollmentException ex,
                                         HttpServletRequest request) {
        return new ErrorResponse(409, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);
        return new ErrorResponse(500, "Internal server error", request.getRequestURI());
    }
}
```

---

## 14. Interview Talking Points

These are things you should be ready to explain clearly in a Cognizant interview.

### "Why did you use both JPA and JDBC in the same project?"

> "For standard CRUD operations like saving a student or a course, JPA repositories are clean and reduce boilerplate. But for the CGPA aggregation — which involves a JOIN across four tables and a weighted average — raw JdbcTemplate gives me full control over the SQL and is more efficient for this kind of read-heavy analytic query. Using both also demonstrates that I understand when to use each tool."

### "What is Spring MVC and how did you use it?"

> "Spring MVC is the web framework that handles HTTP requests using the Model-View-Controller pattern. In this project, the controller methods populate a Model object, and Thymeleaf reads the model to render HTML templates. For example, StudentMvcController.listStudents() fetches students from the service, adds them to the model, and returns the view name 'students/list' — Spring resolves that to the Thymeleaf template automatically."

### "Explain your CGPA calculation."

> "CGPA is a weighted average of grade points. The formula is: sum of (grade_point × credits) divided by sum of credits across all courses. I compute this using a JdbcTemplate query with a SQL aggregation — four table JOINs and SUM functions — and return the result as a DTO. I wrote unit tests that verify edge cases: a student with all outstanding grades should get exactly 10.0, and the arithmetic for mixed grades is verified to two decimal places."

### "How did you handle errors across the application?"

> "I used a @ControllerAdvice class as a global exception handler. Custom exceptions like ResourceNotFoundException and DuplicateEnrollmentException are thrown from the service layer and caught here. For REST endpoints, the handler returns a structured JSON error response with status code and message. All unhandled exceptions are logged with the full stack trace at ERROR level. This separates error-handling logic from business logic."

### "What tests did you write and why?"

> "I wrote 30+ unit tests across five test classes using JUnit 5 and Mockito. The most important tests are on GradeCalculator — a pure utility class — where I tested every boundary: 89.9 vs 90.0, 39.9 vs 40.0. These are the exact boundaries where bugs typically hide. For service tests, I mocked repositories with Mockito so tests run without a database — fast, isolated, and repeatable. I also ran JaCoCo to verify >80% line coverage on the service layer."

---

*Total estimated build time: 12–14 focused days*
*Lines of code (approx): 2,500–3,500 across Java + HTML*
*Test count target: 30+ unit tests*
*JD requirements covered: 10/10*