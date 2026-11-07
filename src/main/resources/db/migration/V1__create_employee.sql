CREATE TABLE employee (
                          id              UUID PRIMARY KEY,
                          employee_number VARCHAR(30)  NOT NULL UNIQUE,
                          full_name       VARCHAR(150) NOT NULL,
                          email           VARCHAR(255) NOT NULL UNIQUE,
                          active          BOOLEAN      NOT NULL DEFAULT TRUE,
                          created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
                          updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);