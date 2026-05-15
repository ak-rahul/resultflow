-- ─────────────────────────────────────────────────────────
-- SRMS Seed Data (safe to re-run with INSERT IGNORE)
-- ─────────────────────────────────────────────────────────

USE srms_db;

-- ─────────────────────────────────────────────
-- Students
-- ─────────────────────────────────────────────
INSERT IGNORE INTO students (roll_number, name, email, phone, department, current_sem, date_of_birth) VALUES
('23CS001', 'Rahul AK',    'rahul@cusat.ac.in',  '9876543210', 'CSE',  3, '2005-03-15'),
('23CS002', 'Priya Nair',  'priya@cusat.ac.in',  '9876543211', 'CSE',  3, '2005-07-22'),
('23EC001', 'Arjun Menon', 'arjun@cusat.ac.in',  '9876543212', 'ECE',  3, '2005-01-10'),
('23CS003', 'Anjali Das',  'anjali@cusat.ac.in', '9876543213', 'CSE',  3, '2005-05-30'),
('22CS001', 'Vivek Kumar', 'vivek@cusat.ac.in',  '9876543214', 'CSE',  5, '2004-09-18');

-- ─────────────────────────────────────────────
-- Courses
-- ─────────────────────────────────────────────
INSERT IGNORE INTO courses (course_code, course_name, credits, department, course_type) VALUES
('CS301', 'Data Structures',           4, 'CSE',  'THEORY'),
('CS302', 'Database Management',       3, 'CSE',  'THEORY'),
('CS303', 'Operating Systems',         4, 'CSE',  'THEORY'),
('CS304', 'Computer Networks',         3, 'CSE',  'THEORY'),
('CS305', 'DS Lab',                    2, 'CSE',  'LAB'),
('CS501', 'Compiler Design',           4, 'CSE',  'THEORY'),
('CS502', 'Machine Learning',          3, 'CSE',  'THEORY'),
('CS503', 'Cloud Computing',           3, 'CSE',  'THEORY'),
('CS504', 'ML Lab',                    2, 'CSE',  'LAB'),
('EC301', 'Digital Electronics',       4, 'ECE',  'THEORY'),
('EC302', 'Signals and Systems',       4, 'ECE',  'THEORY'),
('EC303', 'Digital Electronics Lab',   2, 'ECE',  'LAB');

-- ─────────────────────────────────────────────
-- Enrollments (student 1 = Rahul, sem 3)
-- ─────────────────────────────────────────────
INSERT IGNORE INTO enrollments (student_id, course_id, semester, academic_year) VALUES
-- Rahul, sem 3
(1, 1, 3, '2024-25'),
(1, 2, 3, '2024-25'),
(1, 3, 3, '2024-25'),
(1, 4, 3, '2024-25'),
(1, 5, 3, '2024-25'),
-- Priya, sem 3
(2, 1, 3, '2024-25'),
(2, 2, 3, '2024-25'),
(2, 3, 3, '2024-25'),
-- Arjun, sem 3 (ECE)
(3, 10, 3, '2024-25'),
(3, 11, 3, '2024-25'),
(3, 12, 3, '2024-25'),
-- Vivek, sem 5
(5, 6, 5, '2024-25'),
(5, 7, 5, '2024-25'),
(5, 8, 5, '2024-25'),
(5, 9, 5, '2024-25');

-- ─────────────────────────────────────────────
-- Grades (enrollment_id references above inserts)
-- ─────────────────────────────────────────────
INSERT IGNORE INTO grades (enrollment_id, marks_obtained, grade, grade_point, is_pass) VALUES
-- Rahul's grades (enrollment ids 1-5)
(1, 91.0, 'O',   10.0, TRUE),
(2, 82.0, 'A+',   9.0, TRUE),
(3, 74.0, 'A',    8.0, TRUE),
(4, 65.0, 'B+',   7.0, TRUE),
(5, 88.0, 'O',   10.0, TRUE),
-- Priya's grades (enrollment ids 6-8)
(6, 78.0, 'A',    8.0, TRUE),
(7, 55.0, 'B',    6.0, TRUE),
(8, 36.0, 'F',    0.0, FALSE),
-- Arjun's grades (enrollment ids 9-11)
(9,  85.0, 'A+',  9.0, TRUE),
(10, 70.0, 'A',   8.0, TRUE),
(11, 92.0, 'O',  10.0, TRUE),
-- Vivek's grades (enrollment ids 12-15)
(12, 95.0, 'O',  10.0, TRUE),
(13, 88.0, 'O',  10.0, TRUE),
(14, 73.0, 'A',   8.0, TRUE),
(15, 62.0, 'B+',  7.0, TRUE);
