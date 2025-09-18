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

CREATE TABLE DoctorReviews (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES Patient(patient_id),
    doctor_id UUID NOT NULL REFERENCES Doctor(doctor_id),
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
    doctor_id UUID NOT NULL REFERENCES Doctor(doctor_id),
    exception_date DATE NOT NULL,
    reason VARCHAR(255),
    is_working_day BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(doctor_id, exception_date)
);

-- Таблица слотов времени для записи
CREATE TABLE TimeSlots (
    slot_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doctor_id UUID NOT NULL REFERENCES Doctor(doctor_id),
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

-- Добавляем комментарии к таблицам для документации
COMMENT ON TABLE Doctor IS 'Врачи медицинского центра';
COMMENT ON TABLE Patient IS 'Пациенты медицинского центра';
COMMENT ON TABLE Visit IS 'Визиты пациентов к врачам';
COMMENT ON TABLE Service IS 'Медицинские услуги, предоставляемые врачами';
COMMENT ON TABLE ServiceRendered IS 'Оказанные услуги во время визитов';
COMMENT ON TABLE DoctorReviews IS 'Отзывы пациентов о врачах';
COMMENT ON TABLE TimeSlots IS 'Временные слоты для записи на прием';
COMMENT ON TABLE ScheduleExceptions IS 'Исключения в расписании врачей';
COMMENT ON TABLE VerificationCodes IS 'Коды верификации для регистрации и сброса пароля';
COMMENT ON TABLE Operators IS 'Операторы call-центра';
COMMENT ON TABLE Manager IS 'Менеджеры медицинского центра';

ALTER TABLE ScheduleExceptions
ADD CONSTRAINT fk_scheduleExceptions_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE TimeSlots
ADD CONSTRAINT fk_timeslots_doctor 
FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id);

ALTER TABLE TimeSlots
ADD CONSTRAINT fk_timeslots_visit 
FOREIGN KEY (visit_id) REFERENCES Visit(visit_id);

ALTER TABLE TimeSlots
ADD CONSTRAINT fk_timeslots_visit 
FOREIGN KEY (visit_id) REFERENCES Visit(visit_id);

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

ALTER TABLE VerificationCodes
ADD CONSTRAINT chk_verificationcodes_expires 
CHECK (expires_at > created_at);

ALTER TABLE Operators
ADD CONSTRAINT chk_operators_age 
CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years');

ALTER TABLE Manager
ADD CONSTRAINT chk_manager_age 
CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years');

ALTER TABLE TimeSlots
ADD CONSTRAINT chk_timeslots_time 
CHECK (start_time < end_time);

ALTER TABLE ScheduleExceptions
ADD CONSTRAINT chk_scheduleexceptions_date 
CHECK (exception_date > '2000-01-01');

ALTER TABLE Schedule 
ADD CONSTRAINT check_schedule_time 
CHECK (start_time < end_time);

ALTER TABLE Visit 
ADD CONSTRAINT check_visit_future 
CHECK (date_of_visit > '2000-01-01');

-- Добавляем индексы для улучшения производительности
CREATE INDEX idx_visit_doctor ON Visit(doctor_id);
CREATE INDEX idx_visit_patient ON Visit(patient_id);
CREATE INDEX idx_visit_date ON Visit(date_of_visit);
CREATE INDEX idx_schedule_doctor ON Schedule(doctor_id);
CREATE INDEX idx_schedule_date ON Schedule(work_day);
CREATE INDEX idx_servicerendered_visit ON ServiceRendered(visit_id);
CREATE INDEX idx_servicerendered_service ON ServiceRendered(service_id);
CREATE INDEX idx_service_doctor ON Service(doctor_id);

CREATE INDEX idx_doctorreviews_doctor ON DoctorReviews(doctor_id);
CREATE INDEX idx_doctorreviews_patient ON DoctorReviews(patient_id);
CREATE INDEX idx_doctorreviews_rating ON DoctorReviews(rating);
CREATE INDEX idx_doctorreviews_approved ON DoctorReviews(is_approved);
CREATE INDEX idx_doctorreviews_created ON DoctorReviews(created_at);

CREATE INDEX idx_scheduleexceptions_doctor ON ScheduleExceptions(doctor_id);
CREATE INDEX idx_scheduleexceptions_date ON ScheduleExceptions(exception_date);
CREATE INDEX idx_scheduleexceptions_working ON ScheduleExceptions(is_working_day);

CREATE INDEX idx_timeslots_doctor_date ON TimeSlots(doctor_id, slot_date);
CREATE INDEX idx_timeslots_date ON TimeSlots(slot_date);
CREATE INDEX idx_timeslots_available ON TimeSlots(visit_id) WHERE visit_id IS NULL;
CREATE INDEX idx_timeslots_visit ON TimeSlots(visit_id);

CREATE INDEX idx_verificationcodes_phone ON VerificationCodes(phone);
CREATE INDEX idx_verificationcodes_code ON VerificationCodes(code);
CREATE INDEX idx_verificationcodes_expires ON VerificationCodes(expires_at);
CREATE INDEX idx_verificationcodes_used ON VerificationCodes(is_used);
CREATE INDEX idx_verificationcodes_created ON VerificationCodes(created_at);

CREATE INDEX idx_operators_phone ON Operators(phone);
CREATE INDEX idx_operators_email ON Operators(email);
CREATE INDEX idx_operators_dob ON Operators(date_of_birth);

CREATE INDEX idx_manager_phone ON Manager(phone);
CREATE INDEX idx_manager_email ON Manager(email);
CREATE INDEX idx_manager_dob ON Manager(date_of_birth);

-- Индексы для поиска по именам
CREATE INDEX idx_doctor_name ON Doctor(last_name, first_name);
CREATE INDEX idx_patient_name ON Patient(last_name, first_name);

-- Триггер для автоматического обновления времени
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггеры для обновления времени изменения
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

CREATE TRIGGER trigger_doctorreviews_updated
BEFORE UPDATE ON DoctorReviews
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_operators_updated
BEFORE UPDATE ON Operators
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

CREATE TRIGGER trigger_manager_updated
BEFORE UPDATE ON Manager
FOR EACH ROW
EXECUTE FUNCTION update_modified_time();

-- Триггер для обновления рейтинга врача
CREATE OR REPLACE FUNCTION update_doctor_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Doctor 
    SET average_rating = (
        SELECT AVG(rating)::DECIMAL(3,2)
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

-- Триггер для проверки доступности временного слота
CREATE OR REPLACE FUNCTION check_time_slot_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.visit_id IS NOT NULL THEN
        -- Проверяем, не занят ли уже этот слот другим визитом
        IF EXISTS (
            SELECT 1 FROM TimeSlots 
            WHERE slot_id = NEW.slot_id 
            AND visit_id IS NOT NULL 
            AND visit_id != NEW.visit_id
        ) THEN
            RAISE EXCEPTION 'Time slot is already occupied by another visit';
        END IF;
        
        -- Проверяем, не пересекается ли время с другими визитами врача
        IF EXISTS (
            SELECT 1 FROM TimeSlots ts
            JOIN Visit v ON ts.visit_id = v.visit_id
            WHERE ts.doctor_id = NEW.doctor_id
            AND ts.slot_date = NEW.slot_date
            AND ts.visit_id != NEW.visit_id
            AND ts.start_time < NEW.end_time
            AND ts.end_time > NEW.start_time
        ) THEN
            RAISE EXCEPTION 'Time slot overlaps with another appointment';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_time_slot
BEFORE INSERT OR UPDATE ON TimeSlots
FOR EACH ROW
EXECUTE FUNCTION check_time_slot_availability();
