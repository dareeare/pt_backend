CREATE DATABASE medicalcenter;

-- Включаем расширение для работы с UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

SELECT uuid_generate_v4(); 

CREATE TABLE Doctor (
    doctor_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50) NULL,
    specialty VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE Patient (
    patient_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50) NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    date_of_birth DATE,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')) -- M: Male, F: Female, O: Other
);

CREATE TABLE Service (
    service_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name_of_service VARCHAR(100) NOT NULL,
    cost DECIMAL(10, 2) NOT NULL CHECK (cost >= 0),
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    doctor_id UUID NOT NULL
);

CREATE TABLE Visit (
    visit_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date_of_visit TIMESTAMP NOT NULL,
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    status VARCHAR(20) DEFAULT 'scheduled' CHECK (status IN ('scheduled', 'completed', 'cancelled')),
    symptoms TEXT,
    diagnosis TEXT,
    prescription TEXT
);

CREATE TABLE Schedule (
    schedule_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    doctor_id UUID NOT NULL,
    work_day DATE NOT NULL
);

CREATE TABLE ServiceRendered (
    sr_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL,
    service_id UUID NOT NULL,
    actual_cost DECIMAL(10, 2) NOT NULL CHECK (actual_cost >= 0)
);

-- Теперь добавляем внешние ключи
ALTER TABLE Visit 
ADD CONSTRAINT fk_visit_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE Visit 
ADD CONSTRAINT fk_visit_patient 
FOREIGN KEY (patient_id) REFERENCES Patient(patient_id);

ALTER TABLE Schedule 
ADD CONSTRAINT fk_schedule_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE ServiceRendered 
ADD CONSTRAINT fk_servicerendered_visit 
FOREIGN KEY (visit_id) REFERENCES Visit(visit_id);

ALTER TABLE Service
ADD CONSTRAINT fk_service_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE ServiceRendered 
ADD CONSTRAINT fk_servicerendered_visit 
FOREIGN KEY (visit_id) REFERENCES Visit(visit_id);

CREATE TABLE DoctorReviews (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES Users(user_id),
    doctor_id UUID NOT NULL REFERENCES Doctors(doctor_id),
    appointment_id UUID NOT NULL REFERENCES Visit(visit_id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_approved BOOLEAN DEFAULT FALSE,
    is_edited BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(appointment_id)
);

-- Исключения в расписании (отпуск, больничный)
CREATE TABLE ScheduleExceptions (
    exception_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES Doctors(doctor_id),
    exception_date DATE NOT NULL,
    reason VARCHAR(255),
    is_working_day BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(doctor_id, exception_date)
);

-- Таблица слотов времени для записи
CREATE TABLE TimeSlots (
    slot_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES Doctors(doctor_id),
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    visit_id UUID DEFAULT NULL,
    UNIQUE(doctor_id, slot_date, start_time),
    CHECK (start_time < end_time)
);

CREATE TABLE VerificationCodes (
    code_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    action_type VARCHAR(20) NOT NULL CHECK (action_type IN ('registration', 'password_reset')),
    is_used BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Operators (
	operator_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50) NULL,
  	date_of_birth date NOT NULL,
  	phone VARCHAR(20),
    email VARCHAR(100)
);

CREATE TABLE Manager (
	manager_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50) NULL,
  	date_of_birth date NOT NULL,
  	phone VARCHAR(20),
    email VARCHAR(100)
);

ALTER TABLE ScheduleExceptions
ADD CONSTRAINT fk_scheduleExceptions_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE TimeSlots
ADD CONSTRAINT fk_timeslots_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE TimeSlots
ADD CONSTRAINT fk_timeslots_visit 
FOREIGN KEY (visit_id) REFERENCES Visit(visit_id);

-- Добавляем индексы для улучшения производительности
CREATE INDEX idx_visit_doctor ON Visit(doctor_id);
CREATE INDEX idx_visit_patient ON Visit(patient_id);
CREATE INDEX idx_visit_date ON Visit(date_of_visit);
CREATE INDEX idx_schedule_doctor ON Schedule(doctor_id);
CREATE INDEX idx_schedule_date ON Schedule(work_day);
CREATE INDEX idx_servicerendered_visit ON ServiceRendered(visit_id);
CREATE INDEX idx_servicerendered_service ON ServiceRendered(service_id);
CREATE INDEX idx_service_doctor ON Service(doctor_id);

-- Индексы для поиска по именам
CREATE INDEX idx_doctor_name ON Doctor(last_name, first_name);
CREATE INDEX idx_patient_name ON Patient(last_name, first_name);

-- Добавляем проверки для временных интервалов
ALTER TABLE Schedule 
ADD CONSTRAINT check_schedule_time 
CHECK (start_time < end_time);

ALTER TABLE Visit 
ADD CONSTRAINT check_visit_future 
CHECK (date_of_visit > '2000-01-01');
