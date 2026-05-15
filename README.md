# ResultFlow

> **Built for:** Cognizant DN 5.0 — Programmer Analyst / Programmer Analyst Trainee (Java FSE)
> **JD Coverage:** Java · Spring MVC · Spring Boot · Spring REST · MySQL · JDBC · HTML5 · CSS3 · Bootstrap · JUnit · Mockito · Logging

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Prerequisites](#prerequisites)
5. [Setup & Run](#setup--run)
6. [REST API Reference](#rest-api-reference)
7. [Running Tests](#running-tests)
8. [Grade Mapping](#grade-mapping)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| View Layer | Thymeleaf + Bootstrap 5.3 |
| ORM | Spring Data JPA (Hibernate) |
| Direct DB | Spring JdbcTemplate (CGPA aggregation) |
| Database | MySQL 8.0 |
| Build | Maven |
| Testing | JUnit 5 + Mockito + JaCoCo |
| Logging | SLF4J + Logback |

---

## Features

### Admin Panel (MVC / Thymeleaf)

| Feature | Details |
|---|---|
| **Dashboard** | Stat cards: total students, courses, enrollments, grades entered + recent students table |
| **Student Management** | Add, edit, view, soft-deactivate students; search by name or roll number |
| **Course Management** | Add, edit courses; filter by department; course type (Theory/Lab/Project) |
| **Enrollment** | Enroll a student into a course; duplicate enrollment prevention (409) |
| **Grade Entry** | Batch grade entry per student; live grade preview via JavaScript |
| **Student Detail** | Full transcript view: CGPA badge (green ≥ 8.5 / yellow ≥ 6.5 / red < 6.5), semester accordion, course-level results |

### REST API (JSON)

| Endpoint | Description |
|---|---|
| `GET /api/students` | List all students (optional `?query=` search) |
| `POST /api/students` | Create student (201) |
| `GET /api/students/{rollNo}` | Get student by roll number |
| `PUT /api/students/{rollNo}` | Update student |
| `DELETE /api/students/{rollNo}` | Deactivate student |
| `GET /api/results/{rollNo}` | Full transcript (CGPA + all semesters) |
| `GET /api/results/{rollNo}/cgpa` | CGPA via raw JDBC |
| `GET /api/results/{rollNo}/sem/{sem}` | Single-semester result |
| `GET /api/courses` | List courses (optional `?department=`) |
| `POST /api/courses` | Create course |
| `GET /api/courses/{code}` | Get course by code |

### Unit Tests (JUnit 5 + Mockito)

| Test Class | Tests | Focus |
|---|---|---|
| `GradeCalculatorTest` | 23 | Every grade boundary (39/40, 44/45, 89/90, etc.) |
| `StudentServiceTest` | 7 | CRUD, duplicate roll, soft-delete |
| `GradeServiceTest` | 6 | Grade entry, upsert, invalid enrollment |
| `ResultServiceTest` | 7 | CGPA via mocked JdbcTemplate |
| `EnrollmentServiceTest` | 4 | Enroll, duplicate, not-found cascades |

---

## Project Structure

```
resultflow/
├── src/main/java/com/rahulak/srms/
│   ├── SrmsApplication.java
│   ├── model/          Student, Course, Enrollment, Grade, CourseType
│   ├── repository/     StudentRepository, CourseRepository, EnrollmentRepository, GradeRepository
│   ├── util/           GradeCalculator (pure static, fully unit-testable)
│   ├── dto/            StudentDTO, CourseDTO, GradeEntryDTO, ResultDTO, SemesterResultDTO,
│   │                   CourseResultDTO, CgpaDTO, ErrorResponse
│   ├── exception/      ResourceNotFoundException, DuplicateEnrollmentException,
│   │                   InvalidMarksException, GlobalExceptionHandler
│   ├── service/        StudentService, CourseService, EnrollmentService, GradeService,
│   │                   ResultService (JdbcTemplate CGPA), DashboardService
│   └── controller/
│       ├── mvc/        DashboardMvcController, StudentMvcController, CourseMvcController,
│       │               EnrollmentMvcController, GradeMvcController
│       └── rest/       StudentRestController, CourseRestController, ResultRestController
│
├── src/main/resources/
│   ├── application.properties
│   ├── db/schema.sql   (auto-run on startup)
│   ├── db/data.sql     (seed data, INSERT IGNORE)
│   ├── static/css/srms.css
│   ├── static/js/srms.js
│   └── templates/      index.html, students/, courses/, enrollments/, grades/, error.html
│
└── src/test/java/com/rahulak/srms/
    ├── util/GradeCalculatorTest.java
    └── service/ (StudentServiceTest, GradeServiceTest, ResultServiceTest, EnrollmentServiceTest)
```

---

## Prerequisites

- **Java 17** (JDK) — `java -version`
- **Maven 3.9+** — `mvn -version`
- **MySQL 8.0** running locally on port 3306

---

## Setup & Run

### 1. Create the MySQL database

```sql
CREATE DATABASE IF NOT EXISTS srms_db;
```

Or just run the app — `schema.sql` runs automatically on startup (via `spring.sql.init.mode=always`).

### 2. Configure database credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 3. Run the application

```bash
mvn spring-boot:run
```

Open **http://localhost:8080** in your browser.

### 4. Seed data (automatic)

`data.sql` is loaded automatically. It inserts sample students, courses, enrollments, and grades using `INSERT IGNORE` (safe to re-run).

Sample students loaded:
- `23CS001` — Rahul AK (CSE)
- `23CS002` — Priya Nair (CSE) — has one backlog
- `23EC001` — Arjun Menon (ECE)
- `23CS003` — Anjali Das (CSE) — no grades yet
- `22CS001` — Vivek Kumar (CSE, Sem 5)

---

## REST API Reference

### Get full result (CGPA + all semesters)

```bash
curl http://localhost:8080/api/results/23CS001
```

Response:
```json
{
  "rollNumber": "23CS001",
  "name": "Rahul AK",
  "cgpa": 8.88,
  "totalCourses": 5,
  "backlogs": 0,
  "semesters": [
    {
      "semester": 3,
      "academicYear": "2024-25",
      "gpa": 8.88,
      "totalCredits": 16,
      "courses": [ ... ]
    }
  ]
}
```

### Get CGPA only (raw JDBC)

```bash
curl http://localhost:8080/api/results/23CS001/cgpa
```

### Error response format

```json
{
  "timestamp": "2025-05-15T10:30:00",
  "status": 404,
  "message": "Student not found with roll number: '23CS999'",
  "path": "/api/students/23CS999"
}
```

---

## Running Tests

```bash
# Run all unit tests
mvn test

# Run a specific test class
mvn test -Dtest=GradeCalculatorTest

# Run with JaCoCo coverage report
mvn test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html
```

Target coverage: **>80% on service and util layers.**

---

## Grade Mapping

| Marks | Grade | Grade Point | Description |
|---|---|---|---|
| 90–100 | O | 10.0 | Outstanding |
| 80–89 | A+ | 9.0 | Excellent |
| 70–79 | A | 8.0 | Very Good |
| 60–69 | B+ | 7.0 | Good |
| 50–59 | B | 6.0 | Above Average |
| 45–49 | C | 5.0 | Average |
| 40–44 | P | 4.0 | Pass |
| 0–39 | F | 0.0 | Fail |

**Pass threshold: marks ≥ 40**

**CGPA formula:** `Σ(grade_point × credits) / Σ(credits)` — computed with raw SQL aggregation via `JdbcTemplate`.

---

*Total files: ~55 Java + HTML + SQL + CSS + JS*
*Test count: 47 unit tests*
*JD requirements covered: 10/10*
