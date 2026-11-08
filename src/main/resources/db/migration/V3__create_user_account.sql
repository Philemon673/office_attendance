CREATE TABLE user_account(
    id  UUID PRIMARY KEY,
    employee_id UUID UNIQUE REFERENCES employee (id),
    email VARCHAR(225) NOT NULL UNIQUE CHECK ( email = lower(email) ),
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20) NOT NULL CHECK ( role IN ('EMPLOYEE', 'ADMIN')),
    active        BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    update_at     TIMESTAMPTZ NOT NULL  DEFAULT now()
);