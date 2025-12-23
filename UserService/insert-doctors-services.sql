-- Additional Doctors and Services for Medical Center
-- Execute this script to add more doctors and their services to the database

-- Additional Doctors
INSERT INTO doctor (doctor_id, first_name, last_name, middle_name, specialty, phone, email, information, rating) VALUES
('f0000001-0000-0000-0000-000000000001', 'Robert', 'Taylor', 'William', 'Orthopedic Surgeon', '+1234560006', 'robert.taylor@medcenter.com', '20 years of experience in orthopedic surgery and joint replacement', 4.9),
('f0000002-0000-0000-0000-000000000002', 'Jennifer', 'Martinez', 'Lynn', 'Gynecologist', '+1234560007', 'jennifer.martinez@medcenter.com', '15 years specializing in women''s health and reproductive medicine', 4.8),
('f0000003-0000-0000-0000-000000000003', 'Christopher', 'Lee', 'James', 'Ophthalmologist', '+1234560008', 'christopher.lee@medcenter.com', '12 years in eye care and vision correction', 4.7),
('f0000004-0000-0000-0000-000000000004', 'Amanda', 'White', 'Nicole', 'Psychiatrist', '+1234560009', 'amanda.white@medcenter.com', '10 years in mental health and therapy', 4.6),
('f0000005-0000-0000-0000-000000000005', 'Daniel', 'Harris', 'Paul', 'Endocrinologist', '+1234560010', 'daniel.harris@medcenter.com', '14 years specializing in diabetes and hormonal disorders', 4.8),
('f0000006-0000-0000-0000-000000000006', 'Maria', 'Garcia', 'Elena', 'Pulmonologist', '+1234560011', 'maria.garcia@medcenter.com', '11 years in respiratory medicine and lung diseases', 4.7),
('f0000007-0000-0000-0000-000000000007', 'Thomas', 'Wilson', 'Edward', 'Urologist', '+1234560012', 'thomas.wilson@medcenter.com', '13 years in urology and men''s health', 4.9),
('f0000008-0000-0000-0000-000000000008', 'Jessica', 'Moore', 'Ann', 'Gastroenterologist', '+1234560013', 'jessica.moore@medcenter.com', '9 years in digestive system disorders', 4.6);

-- Additional Services for Dr. Robert Taylor (Orthopedic Surgeon)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000001-0000-0000-0000-000000000001', 'Orthopedic Consultation', 100.00, 45, 'Initial consultation with orthopedic examination', 'f0000001-0000-0000-0000-000000000001'),
('b0000002-0000-0000-0000-000000000002', 'Joint Injection', 150.00, 30, 'Therapeutic injection for joint pain relief', 'f0000001-0000-0000-0000-000000000001'),
('b0000003-0000-0000-0000-000000000003', 'Knee Arthroscopy Consultation', 200.00, 60, 'Pre-surgical consultation for knee arthroscopy', 'f0000001-0000-0000-0000-000000000001'),
('b0000004-0000-0000-0000-000000000004', 'Fracture Follow-up', 80.00, 30, 'Post-treatment follow-up for fractures', 'f0000001-0000-0000-0000-000000000001');

-- Additional Services for Dr. Jennifer Martinez (Gynecologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000005-0000-0000-0000-000000000005', 'Gynecological Examination', 90.00, 45, 'Comprehensive women''s health examination', 'f0000002-0000-0000-0000-000000000002'),
('b0000006-0000-0000-0000-000000000006', 'Prenatal Consultation', 110.00, 60, 'Pregnancy consultation and monitoring', 'f0000002-0000-0000-0000-000000000002'),
('b0000007-0000-0000-0000-000000000007', 'Ultrasound Examination', 95.00, 30, 'Gynecological ultrasound scan', 'f0000002-0000-0000-0000-000000000002'),
('b0000008-0000-0000-0000-000000000008', 'Contraception Consultation', 70.00, 30, 'Consultation on birth control options', 'f0000002-0000-0000-0000-000000000002');

-- Additional Services for Dr. Christopher Lee (Ophthalmologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000009-0000-0000-0000-000000000009', 'Eye Examination', 85.00, 45, 'Comprehensive eye health examination', 'f0000003-0000-0000-0000-000000000003'),
('b000000a-0000-0000-0000-00000000000a', 'Vision Test', 50.00, 30, 'Standard vision acuity test', 'f0000003-0000-0000-0000-000000000003'),
('b000000b-0000-0000-0000-00000000000b', 'Retinal Examination', 120.00, 45, 'Detailed retinal examination with dilation', 'f0000003-0000-0000-0000-000000000003'),
('b000000c-0000-0000-0000-00000000000c', 'Glaucoma Screening', 75.00, 30, 'Screening for glaucoma and eye pressure check', 'f0000003-0000-0000-0000-000000000003');

-- Additional Services for Dr. Amanda White (Psychiatrist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b000000d-0000-0000-0000-00000000000d', 'Psychiatric Consultation', 130.00, 60, 'Initial psychiatric evaluation and assessment', 'f0000004-0000-0000-0000-000000000004'),
('b000000e-0000-0000-0000-00000000000e', 'Therapy Session', 100.00, 50, 'Individual psychotherapy session', 'f0000004-0000-0000-0000-000000000004'),
('b000000f-0000-0000-0000-00000000000f', 'Follow-up Consultation', 90.00, 30, 'Follow-up psychiatric consultation', 'f0000004-0000-0000-0000-000000000004'),
('b0000010-0000-0000-0000-000000000010', 'Medication Review', 80.00, 30, 'Review and adjustment of psychiatric medications', 'f0000004-0000-0000-0000-000000000004');

-- Additional Services for Dr. Daniel Harris (Endocrinologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000011-0000-0000-0000-000000000011', 'Endocrinology Consultation', 110.00, 45, 'Consultation for hormonal and metabolic disorders', 'f0000005-0000-0000-0000-000000000005'),
('b0000012-0000-0000-0000-000000000012', 'Diabetes Management', 95.00, 40, 'Diabetes consultation and treatment planning', 'f0000005-0000-0000-0000-000000000005'),
('b0000013-0000-0000-0000-000000000013', 'Thyroid Function Analysis', 85.00, 30, 'Thyroid examination and function assessment', 'f0000005-0000-0000-0000-000000000005'),
('b0000014-0000-0000-0000-000000000014', 'Hormone Level Review', 100.00, 35, 'Review of hormone test results and treatment', 'f0000005-0000-0000-0000-000000000005');

-- Additional Services for Dr. Maria Garcia (Pulmonologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000015-0000-0000-0000-000000000015', 'Pulmonology Consultation', 105.00, 45, 'Consultation for respiratory system disorders', 'f0000006-0000-0000-0000-000000000006'),
('b0000016-0000-0000-0000-000000000016', 'Lung Function Test', 120.00, 40, 'Spirometry and lung capacity testing', 'f0000006-0000-0000-0000-000000000006'),
('b0000017-0000-0000-0000-000000000017', 'Asthma Management', 90.00, 35, 'Asthma consultation and treatment plan', 'f0000006-0000-0000-0000-000000000006'),
('b0000018-0000-0000-0000-000000000018', 'Chest X-ray Review', 75.00, 25, 'Review and interpretation of chest imaging', 'f0000006-0000-0000-0000-000000000006');

-- Additional Services for Dr. Thomas Wilson (Urologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b0000019-0000-0000-0000-000000000019', 'Urology Consultation', 100.00, 45, 'Consultation for urinary and reproductive system issues', 'f0000007-0000-0000-0000-000000000007'),
('b000001a-0000-0000-0000-00000000001a', 'Prostate Examination', 90.00, 30, 'Prostate health examination and screening', 'f0000007-0000-0000-0000-000000000007'),
('b000001b-0000-0000-0000-00000000001b', 'Kidney Function Assessment', 95.00, 35, 'Assessment of kidney function and health', 'f0000007-0000-0000-0000-000000000007'),
('b000001c-0000-0000-0000-00000000001c', 'Men''s Health Consultation', 85.00, 40, 'Comprehensive men''s health consultation', 'f0000007-0000-0000-0000-000000000007');

-- Additional Services for Dr. Jessica Moore (Gastroenterologist)
INSERT INTO service (service_id, name_of_service, cost, duration_minutes, information, doctor_id) VALUES
('b000001d-0000-0000-0000-00000000001d', 'Gastroenterology Consultation', 95.00, 45, 'Consultation for digestive system disorders', 'f0000008-0000-0000-0000-000000000008'),
('b000001e-0000-0000-0000-00000000001e', 'Endoscopy Consultation', 150.00, 30, 'Pre-procedure consultation for endoscopy', 'f0000008-0000-0000-0000-000000000008'),
('b000001f-0000-0000-0000-00000000001f', 'IBS Management', 85.00, 40, 'Irritable bowel syndrome consultation and treatment', 'f0000008-0000-0000-0000-000000000008'),
('b0000020-0000-0000-0000-000000000020', 'Digestive Health Review', 80.00, 35, 'Review of digestive health and dietary recommendations', 'f0000008-0000-0000-0000-000000000008');

COMMIT;

