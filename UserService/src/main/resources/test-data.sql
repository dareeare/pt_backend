-- Test data for Medical Center (with correct UUID format)

-- Patients
INSERT INTO patient (patient_id, first_name, last_name, middle_name, date_of_birth, phone, email, gender) VALUES
('11111111-1111-1111-1111-111111111111', 'Michael', 'Smith', 'James', '1985-05-15', '+1234567890', 'michael.smith@email.com', 'M'),
('22222222-2222-2222-2222-222222222222', 'Sarah', 'Johnson', 'Ann', '1990-08-22', '+1234567891', 'sarah.johnson@email.com', 'F'),
('33333333-3333-3333-3333-333333333333', 'David', 'Williams', 'Robert', '1978-12-10', '+1234567892', 'david.williams@email.com', 'M'),
('44444444-4444-4444-4444-444444444444', 'Emily', 'Brown', 'Grace', '1995-03-05', '+1234567893', 'emily.brown@email.com', 'F'),
('55555555-5555-5555-5555-555555555555', 'James', 'Davis', 'Michael', '1982-07-18', '+1234567894', 'james.davis@email.com', 'M');

-- Doctors
INSERT INTO doctor (doctor_id, first_name, last_name, middle_name, specialty, phone, email, information, rating) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Emily', 'Carter', 'Rose', 'General Practitioner', '+1234560001', 'emily.carter@medcenter.com', '12 years of experience in general medicine', 4.8),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Sarah', 'Mitchell', 'Jane', 'Pediatrician', '+1234560002', 'sarah.mitchell@medcenter.com', '8 years specializing in pediatric care', 4.6),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Michael', 'Johnson', 'David', 'Cardiologist', '+1234560003', 'michael.johnson@medcenter.com', '15 years of cardiovascular expertise', 4.9),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'David', 'Brown', 'Thomas', 'Neurologist', '+1234560004', 'david.brown@medcenter.com', '10 years in neurology', 4.7),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Lisa', 'Anderson', 'Marie', 'Dermatologist', '+1234560005', 'lisa.anderson@medcenter.com', '7 years in dermatology', 4.5);

-- Operators
INSERT INTO operators (operator_id, first_name, last_name, middle_name, date_of_birth, phone, email) VALUES
('00000000-0000-0000-0000-000000000010', 'John', 'Operator', 'William', '1992-03-20', '+1234560010', 'john.operator@medcenter.com');

-- Managers
INSERT INTO manager (manager_id, first_name, last_name, middle_name, date_of_birth, phone, email) VALUES
('f0000000-0000-0000-0000-000000000020', 'Alice', 'Manager', 'Elizabeth', '1988-11-15', '+1234560020', 'alice.manager@medcenter.com');

-- Services (each service belongs to a doctor)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('a0000001-0000-0000-0000-000000000001', 'General Practitioner Consultation', 50.00, 30, 'Initial consultation with a general practitioner', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('a0000002-0000-0000-0000-000000000002', 'Pediatric Consultation', 55.00, 30, 'Consultation with a pediatrician', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('a0000003-0000-0000-0000-000000000003', 'Cardiology Examination', 120.00, 60, 'Complete cardiovascular system examination', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('a0000004-0000-0000-0000-000000000004', 'Neurological Examination', 80.00, 45, 'Nervous system examination', 'dddddddd-dddd-dddd-dddd-dddddddddddd'),
('a0000005-0000-0000-0000-000000000005', 'Dermatology Consultation', 60.00, 30, 'Consultation for skin problems', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
('a0000006-0000-0000-0000-000000000006', 'Vaccination', 40.00, 15, 'Preventive vaccination', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'),
('a0000007-0000-0000-0000-000000000007', 'Laboratory Tests', 75.00, 20, 'General and biochemical blood analysis', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

-- Past visits (completed) - diagnosis is NOT NULL!
INSERT INTO visit (visit_id, date_of_visit, doctor_id, patient_id, status, symptoms, diagnosis, prescription) VALUES
-- Michael Smith's visits
('d0000001-0001-0001-0001-000000000001', '2025-09-15 10:00:00', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'completed', 'Cough, fever 37.5C', 'Acute respiratory infection', 'Antiviral medication, bed rest for 5 days'),
('d0000002-0002-0002-0002-000000000002', '2025-10-20 14:30:00', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'completed', 'Headache, fatigue', 'Overwork syndrome', 'Rest, vitamin B complex'),
('d0000003-0003-0003-0003-000000000003', '2025-10-28 09:00:00', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'completed', 'Chest pain', 'Functional heart disorder', 'Sedatives, monitoring required'),

-- Sarah Johnson's visits
('d0000004-0004-0004-0004-000000000004', '2025-09-10 11:00:00', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'completed', 'Routine checkup', 'Healthy', 'Dietary recommendations'),
('d0000005-0005-0005-0005-000000000005', '2025-10-15 15:00:00', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'completed', 'Allergic rash', 'Contact dermatitis', 'Antihistamines, avoid allergen'),

-- David Williams' visits
('d0000006-0006-0006-0006-000000000006', '2025-09-25 13:00:00', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 'completed', 'Dizziness, migraine', 'Migraine with aura', 'Triptans for attacks, prevention therapy'),
('d0000007-0007-0007-0007-000000000007', '2025-10-30 10:30:00', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 'completed', 'Follow-up examination', 'Condition improved', 'Continue treatment'),

-- Emily Brown's visit
('d0000008-0008-0008-0008-000000000008', '2025-10-05 16:00:00', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', 'completed', 'Acne, skin problems', 'Acne vulgaris', 'Topical treatment, diet adjustment');

-- Future visits (scheduled) - diagnosis cannot be NULL, use placeholder
INSERT INTO visit (visit_id, date_of_visit, doctor_id, patient_id, status, symptoms, diagnosis, prescription) VALUES
-- Michael Smith's future visits
('e0000001-1001-1001-1001-000000001001', '2025-11-15 10:00:00', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'scheduled', '', 'Pending examination', NULL),
('e0000002-1002-1002-1002-000000001002', '2025-11-20 14:00:00', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'scheduled', '', 'Pending examination', NULL),

-- Sarah Johnson's future visit
('e0000003-1003-1003-1003-000000001003', '2025-11-12 11:30:00', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'scheduled', '', 'Pending examination', NULL),

-- David Williams' future visits
('e0000004-1004-1004-1004-000000001004', '2025-11-18 09:00:00', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 'scheduled', '', 'Pending examination', NULL),
('e0000005-1005-1005-1005-000000001005', '2025-12-01 15:30:00', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 'scheduled', '', 'Pending examination', NULL),

-- Emily Brown's future visit
('e0000006-1006-1006-1006-000000001006', '2025-11-25 10:30:00', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '44444444-4444-4444-4444-444444444444', 'scheduled', '', 'Pending examination', NULL),

-- James Davis' future visit
('e0000007-1007-1007-1007-000000001007', '2025-11-22 13:00:00', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-5555-5555-5555-555555555555', 'scheduled', '', 'Pending examination', NULL);

-- Doctor schedules (specific dates with full datetime)
INSERT INTO schedule (schedule_id, doctor_id, work_day, start_time, end_time) VALUES
-- Dr. Emily Carter - November 2025
('b0000001-0001-0001-0001-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-11-11', '2025-11-11 09:00:00', '2025-11-11 17:00:00'),
('b0000002-0002-0002-0002-000000000002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-11-13', '2025-11-13 09:00:00', '2025-11-13 17:00:00'),
('b0000003-0003-0003-0003-000000000003', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-11-15', '2025-11-15 09:00:00', '2025-11-15 17:00:00'),

-- Dr. Sarah Mitchell
('b0000004-0004-0004-0004-000000000004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-11-12', '2025-11-12 08:00:00', '2025-11-12 16:00:00'),
('b0000005-0005-0005-0005-000000000005', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-11-14', '2025-11-14 08:00:00', '2025-11-14 16:00:00'),

-- Dr. Michael Johnson
('b0000006-0006-0006-0006-000000000006', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '2025-11-11', '2025-11-11 10:00:00', '2025-11-11 18:00:00'),
('b0000007-0007-0007-0007-000000000007', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '2025-11-12', '2025-11-12 10:00:00', '2025-11-12 18:00:00'),
('b0000008-0008-0008-0008-000000000008', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '2025-11-20', '2025-11-20 10:00:00', '2025-11-20 18:00:00'),

-- Dr. David Brown
('b0000009-0009-0009-0009-000000000009', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '2025-11-18', '2025-11-18 09:00:00', '2025-11-18 15:00:00'),
('b000000a-000a-000a-000a-00000000000a', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '2025-12-01', '2025-12-01 09:00:00', '2025-12-01 15:00:00'),

-- Dr. Lisa Anderson
('b000000b-000b-000b-000b-00000000000b', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '2025-11-25', '2025-11-25 10:00:00', '2025-11-25 16:00:00');

-- Doctor reviews (with visit_id reference)
INSERT INTO doctorreviews (review_id, doctor_id, patient_id, visit_id, rating, comment, is_approved, is_edited, created_at, updated_at) VALUES
('c0000001-0001-0001-0001-000000000001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'd0000001-0001-0001-0001-000000000001', 5, 'Excellent doctor, very attentive and professional!', true, false, '2025-09-16 10:00:00', '2025-09-16 10:00:00'),
('c0000002-0002-0002-0002-000000000002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'd0000002-0002-0002-0002-000000000002', 5, 'Quick diagnosis, treatment helped', true, false, '2025-10-21 14:00:00', '2025-10-21 14:00:00'),
('c0000003-0003-0003-0003-000000000003', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'd0000004-0004-0004-0004-000000000004', 4, 'Good pediatrician, but had to wait long for appointment', true, false, '2025-09-11 12:00:00', '2025-09-11 12:00:00'),
('c0000004-0004-0004-0004-000000000004', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'd0000003-0003-0003-0003-000000000003', 5, 'True professional, highly recommend!', true, false, '2025-10-29 10:00:00', '2025-10-29 10:00:00'),
('c0000005-0005-0005-0005-000000000005', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', 'd0000007-0007-0007-0007-000000000007', 5, 'Helped get rid of migraines, thank you!', true, false, '2025-10-31 11:00:00', '2025-10-31 11:00:00');

-- Chat messages (all columns in snake_case, type can be: USER, OPERATOR, SYSTEM)
INSERT INTO chat_messages (id, sender_id, sender_name, content, timestamp, type, attachment_url) VALUES
('f0000001-0001-0001-0001-000000000001', NULL, 'system', 'Chat system started', '2025-11-01 09:00:00', 'SYSTEM', NULL),
('f0000002-0002-0002-0002-000000000002', '11111111-1111-1111-1111-111111111111', 'Michael Smith', 'Hello, how can I book an appointment?', '2025-11-05 14:30:00', 'USER', NULL),
('f0000003-0003-0003-0003-000000000003', '00000000-0000-0000-0000-000000000010', 'John Operator', 'Hello! You can book through our website or call us.', '2025-11-05 14:32:00', 'OPERATOR', NULL),
('f0000004-0004-0004-0004-000000000004', '22222222-2222-2222-2222-222222222222', 'Sarah Johnson', 'Can I reschedule my appointment?', '2025-11-06 10:15:00', 'USER', NULL),
('f0000005-0005-0005-0005-000000000005', '00000000-0000-0000-0000-000000000010', 'John Operator', 'Yes, of course. Please provide your appointment number.', '2025-11-06 10:17:00', 'OPERATOR', NULL);

COMMIT;
