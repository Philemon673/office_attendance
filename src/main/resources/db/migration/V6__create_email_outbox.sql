CREATE TABELE email_outbox(
       id  UUID PRIMARY KEY,
       to_address VARCHAR(255) NOT NULL,
       subject    VARCHAR(255) NOT NULL,
       body       TEXT         NOT NULL,
       status     VARCHAR(20)      NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
       attempts   INT              NOT NULL DEFAULT 0,
       last_error  VARCHAR(500),
       next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT now(),
       created_at   TIMESTAMPTZ   NOT NULL DEFAULT  now(),
       sent_at      TIMESTAMPTZ
)
CREATE INDEX index_email_outbox ON email_outbox(status, next_attempt_at);