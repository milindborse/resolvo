-- V1__create_schema.sql
-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    flat_number VARCHAR(200) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE UNIQUE INDEX idx_user_email ON users(email);

-- Create complaints table
CREATE TABLE complaints (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) NOT NULL DEFAULT 'LOW',
    image_url VARCHAR(500),
    resident_id BIGINT NOT NULL,
    suggested_priority VARCHAR(20),
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    overdue BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_complaints_resident FOREIGN KEY (resident_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_complaint_status ON complaints(status);
CREATE INDEX idx_complaint_priority ON complaints(priority);
CREATE INDEX idx_complaint_category ON complaints(category);
CREATE INDEX idx_complaint_created_at ON complaints(created_at);
CREATE INDEX idx_complaint_overdue ON complaints(overdue);

-- Create complaint_history table
CREATE TABLE complaint_history (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    complaint_id BIGINT NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    actor_id BIGINT NOT NULL,
    remarks VARCHAR(1000),
    CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_history_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_history_complaint ON complaint_history(complaint_id);

-- Create notices table
CREATE TABLE notices (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    title VARCHAR(150) NOT NULL,
    body VARCHAR(3000) NOT NULL,
    important BOOLEAN NOT NULL DEFAULT FALSE,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_notice_important ON notices(important);
CREATE INDEX idx_notice_pinned ON notices(pinned);
CREATE INDEX idx_notice_published ON notices(published);
CREATE INDEX idx_notice_deleted ON notices(deleted);

-- Create notifications table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL,
    reference_id BIGINT,
    read BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_notification_user_read ON notifications(user_id, read);
CREATE INDEX idx_notification_created_at ON notifications(created_at);
