CREATE TABLE attendance_event (
                                  id          UUID PRIMARY KEY,
                                  employee_id UUID        NOT NULL REFERENCES employee (id),
                                  type        VARCHAR(20) NOT NULL CHECK (type IN ('CHECK_IN', 'CHECK_OUT')),
                                  occurred_at TIMESTAMPTZ NOT NULL,
                                  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_attendance_event_employee_time ON attendance_event (employee_id, occurred_at);

CREATE TABLE attendance_record (
                                   id             UUID PRIMARY KEY,
                                   employee_id    UUID        NOT NULL REFERENCES employee (id),
                                   shift_id       UUID        NOT NULL REFERENCES shift (id),
                                   work_date      DATE        NOT NULL,
                                   check_in_at    TIMESTAMPTZ NOT NULL,
                                   check_out_at   TIMESTAMPTZ,
                                   status         VARCHAR(20) NOT NULL CHECK (status IN ('ON_TIME', 'LATE', 'MISSING_CHECKOUT')),
                                   late_minutes   INT         NOT NULL DEFAULT 0 CHECK (late_minutes >= 0),
                                   worked_minutes INT         CHECK (worked_minutes >= 0),
                                   created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                                   updated_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
                                   UNIQUE (employee_id, work_date),
                                   CHECK (check_out_at IS NULL OR check_out_at >= check_in_at)
);

CREATE INDEX idx_attendance_record_work_date ON attendance_record (work_date);