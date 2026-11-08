CREATE TABLE shift (
                       id            UUID PRIMARY KEY,
                       name          VARCHAR(80) NOT NULL UNIQUE,
                       start_time    TIME        NOT NULL,
                       end_time      TIME        NOT NULL,
                       grace_minutes INT         NOT NULL DEFAULT 0 CHECK (grace_minutes >= 0),
                       created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
                       CHECK (end_time > start_time)
);

CREATE TABLE roster_entry (
                              id          UUID PRIMARY KEY,
                              employee_id UUID        NOT NULL REFERENCES employee (id),
                              shift_id    UUID        NOT NULL REFERENCES shift (id),
                              work_date   DATE        NOT NULL,
                              created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
                              UNIQUE (employee_id, work_date)
);

CREATE INDEX idx_roster_entry_work_date ON roster_entry (work_date);