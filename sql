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

-- Добавляем недостающие внешние ключи
ALTER TABLE DoctorReviews
ADD CONSTRAINT fk_doctorreviews_patient 
FOREIGN KEY (patient_id) REFERENCES Patient(patient_id);

ALTER TABLE DoctorReviews
ADD CONSTRAINT fk_doctorreviews_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE DoctorReviews
ADD CONSTRAINT fk_doctorreviews_visit 
FOREIGN KEY (appointment_id) REFERENCES Visit(visit_id);

-- Добавляем недостающие индексы
CREATE INDEX idx_doctorreviews_doctor ON DoctorReviews(doctor_id);
CREATE INDEX idx_doctorreviews_patient ON DoctorReviews(patient_id);
CREATE INDEX idx_doctorreviews_rating ON DoctorReviews(rating);
CREATE INDEX idx_doctorreviews_approved ON DoctorReviews(is_approved);

CREATE INDEX idx_scheduleexceptions_doctor ON ScheduleExceptions(doctor_id);
CREATE INDEX idx_scheduleexceptions_date ON ScheduleExceptions(exception_date);

CREATE INDEX idx_timeslots_doctor_date ON TimeSlots(doctor_id, slot_date);
CREATE INDEX idx_timeslots_available ON TimeSlots(visit_id) WHERE visit_id IS NULL;
CREATE INDEX idx_timeslots_visit ON TimeSlots(visit_id);

CREATE INDEX idx_verificationcodes_phone ON VerificationCodes(phone);
CREATE INDEX idx_verificationcodes_expires ON VerificationCodes(expires_at);
CREATE INDEX idx_verificationcodes_used ON VerificationCodes(is_used);

CREATE INDEX idx_operators_phone ON Operators(phone);
CREATE INDEX idx_operators_email ON Operators(email);

CREATE INDEX idx_manager_phone ON Manager(phone);
CREATE INDEX idx_manager_email ON Manager(email);

-- Добавляем ограничения
ALTER TABLE TimeSlots
ADD CONSTRAINT chk_timeslots_time 
CHECK (start_time < end_time);

ALTER TABLE ScheduleExceptions
ADD CONSTRAINT chk_scheduleexceptions_date 
CHECK (exception_date > '2000-01-01');

ALTER TABLE VerificationCodes
ADD CONSTRAINT chk_verificationcodes_expires 
CHECK (expires_at > created_at);

ALTER TABLE Operators
ADD CONSTRAINT chk_operators_age 
CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years');

ALTER TABLE Manager
ADD CONSTRAINT chk_manager_age 
CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years');

-- Добавляем триггеры

-- Триггер для автоматического обновления времени в таблицах
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Добавляем updated_at в таблицы, где его нет
ALTER TABLE Doctor ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Patient ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Service ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Visit ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE Schedule ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ServiceRendered ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Создаем триггеры для обновления времени
CREATE TRIGGER trigger_doctor_updated
BEFORE UPDATE ON Doctor
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_patient_updated
BEFORE UPDATE ON Patient
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_service_updated
BEFORE UPDATE ON Service
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_visit_updated
BEFORE UPDATE ON Visit
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_schedule_updated
BEFORE UPDATE ON Schedule
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_servicerendered_updated
BEFORE UPDATE ON ServiceRendered
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

-- Триггер для проверки доступности времени при записи
CREATE OR REPLACE FUNCTION check_time_slot_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.visit_id IS NOT NULL THEN
        -- Проверяем, не занят ли уже этот слот
        IF EXISTS (
            SELECT 1 FROM TimeSlots 
            WHERE slot_id = NEW.slot_id 
            AND visit_id IS NOT NULL 
            AND visit_id != NEW.visit_id
        ) THEN
            RAISE EXCEPTION 'Time slot is already occupied';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_time_slot
BEFORE UPDATE ON TimeSlots
FOR EACH ROW
EXECUTE FUNCTION check_time_slot_availability();

-- Триггер для автоматического обновления рейтинга врача
CREATE OR REPLACE FUNCTION update_doctor_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Doctor 
    SET average_rating = (
        SELECT AVG(rating) 
        FROM DoctorReviews 
        WHERE doctor_id = NEW.doctor_id AND is_approved = TRUE
    ),
    updated_at = CURRENT_TIMESTAMP
    WHERE doctor_id = NEW.doctor_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_doctor_rating
AFTER INSERT OR UPDATE ON DoctorReviews
FOR EACH ROW
EXECUTE FUNCTION update_doctor_rating();

-- Триггер для проверки дублирования времени у врача
CREATE OR REPLACE FUNCTION prevent_double_booking()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM Visit v
        JOIN TimeSlots ts ON v.visit_id = ts.visit_id
        WHERE v.doctor_id = NEW.doctor_id
        AND v.visit_id != NEW.visit_id
        AND ts.slot_date = (SELECT slot_date FROM TimeSlots WHERE visit_id = NEW.visit_id)
        AND ts.start_time = (SELECT start_time FROM TimeSlots WHERE visit_id = NEW.visit_id)
    ) THEN
        RAISE EXCEPTION 'Doctor already has an appointment at this time';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_prevent_double_booking
BEFORE INSERT OR UPDATE ON Visit
FOR EACH ROW
EXECUTE FUNCTION prevent_double_booking();

-- Триггер для автоматической пометки использованных кодов верификации
CREATE OR REPLACE FUNCTION mark_verification_code_used()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_used = TRUE THEN
        NEW.used_at = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Добавляем колонку used_at
ALTER TABLE VerificationCodes ADD COLUMN used_at TIMESTAMP;

CREATE TRIGGER trigger_mark_verification_code_used
BEFORE UPDATE ON VerificationCodes
FOR EACH ROW
EXECUTE FUNCTION mark_verification_code_used();

-- Триггер для логирования изменений статуса визитов
CREATE OR REPLACE FUNCTION log_visit_status_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO VisitHistory (visit_id, old_status, new_status, changed_at)
        VALUES (NEW.visit_id, OLD.status, NEW.status, CURRENT_TIMESTAMP);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создаем таблицу для истории изменений визитов
CREATE TABLE VisitHistory (
    history_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    visit_id UUID NOT NULL REFERENCES Visit(visit_id),
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_visithistory_visit ON VisitHistory(visit_id);
CREATE INDEX idx_visithistory_date ON VisitHistory(changed_at);

CREATE TRIGGER trigger_log_visit_status_change
AFTER UPDATE ON Visit
FOR EACH ROW
EXECUTE FUNCTION log_visit_status_change();

-- Добавляем ограничение уникальности для email там, где это необходимо
ALTER TABLE Doctor ADD CONSTRAINT uk_doctor_email UNIQUE (email);
ALTER TABLE Patient ADD CONSTRAINT uk_patient_email UNIQUE (email);
ALTER TABLE Operators ADD CONSTRAINT uk_operators_email UNIQUE (email);
ALTER TABLE Manager ADD CONSTRAINT uk_manager_email UNIQUE (email);

-- Добавляем ограничение уникальности для телефона
ALTER TABLE Doctor ADD CONSTRAINT uk_doctor_phone UNIQUE (phone);
ALTER TABLE Patient ADD CONSTRAINT uk_patient_phone UNIQUE (phone);
ALTER TABLE Operators ADD CONSTRAINT uk_operators_phone UNIQUE (phone);
ALTER TABLE Manager ADD CONSTRAINT uk_manager_phone UNIQUE (phone);
