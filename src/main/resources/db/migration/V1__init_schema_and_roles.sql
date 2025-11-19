-- Flyway migration: initial schema and default roles
-- PostgreSQL

-- Enable UUID generation (used for seeding roles)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Clean up existing objects so migration can run idempotently
-- DROP TABLE IF EXISTS project_employee CASCADE;
-- DROP TABLE IF EXISTS user_projects CASCADE;
-- DROP TABLE IF EXISTS user_roles CASCADE;
-- DROP TABLE IF EXISTS projects CASCADE;
-- DROP TABLE IF EXISTS pm_users CASCADE;
-- DROP TABLE IF EXISTS roles CASCADE;

-- =========================
--  CORE TABLES
-- =========================

CREATE TABLE roles (
    id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE pm_users (
    id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(50),
    address VARCHAR(255),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_pm_users PRIMARY KEY (id)
);

CREATE TABLE projects (
    id UUID         NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_deleted BOOLEAN      NOT NULL DEFAULT FALSE,
    is_active BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

-- =========================
--  JOIN TABLES
-- =========================

-- User ↔ Role
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES pm_users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON DELETE CASCADE
);

-- User ↔ Project (from User entity mapping: user_projects)
CREATE TABLE user_projects (
    user_id UUID NOT NULL,
    project_id UUID NOT NULL,
    CONSTRAINT pk_user_projects PRIMARY KEY (user_id, project_id),
    CONSTRAINT fk_user_projects_user
        FOREIGN KEY (user_id) REFERENCES pm_users (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_projects_project
        FOREIGN KEY (project_id) REFERENCES projects (id)
        ON DELETE CASCADE
);

-- Project ↔ User (from Project entity mapping: project_employee)
CREATE TABLE project_employee (
    project_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    CONSTRAINT pk_project_employee PRIMARY KEY (project_id, employee_id),
    CONSTRAINT fk_project_employee_project
        FOREIGN KEY (project_id) REFERENCES projects (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_project_employee_user
        FOREIGN KEY (employee_id) REFERENCES pm_users (id)
        ON DELETE CASCADE
);

-- =========================
--  DEFAULT ROLES
-- =========================

INSERT INTO roles (id, name, description, is_deleted)
VALUES
    (gen_random_uuid(), 'EMPLOYEE', 'Default employee role', FALSE),
    (gen_random_uuid(), 'PROJECT_MANAGER', 'Project manager role',  FALSE),
    (gen_random_uuid(), 'ADMIN', 'Administrator role',    FALSE);


