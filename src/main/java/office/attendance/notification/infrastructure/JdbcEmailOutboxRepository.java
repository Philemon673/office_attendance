package office.attendance.notification.infrastructure;

import office.attendance.notification.domain.EmailOutboxMessage;
import office.attendance.notification.domain.EmailOutboxRepository;
import office.attendance.notification.domain.OutboxStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class JdbcEmailOutboxRepository implements EmailOutboxRepository {
    private static final RowMapper<EmailOutboxMessage> MAPPER = (rs, i) -> new EmailOutboxMessage(
            rs.getObject("id", UUID.class),
            rs.getString("to_address"),
            rs.getString("subject"),
            rs.getString("body"),
            OutboxStatus.valueOf(rs.getString("status")),
            rs.getInt("attempts"));

    private final JdbcTemplate jdbc;

    public JdbcEmailOutboxRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(EmailOutboxMessage m) {
        jdbc.update("""
                INSERT INTO email_outbox (id, to_address, subject, body, status, attempts)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                m.id(), m.toAddress(), m.subject(), m.body(), m.status().name(), m.attempts());
    }

    @Override
    public List<EmailOutboxMessage> lockDue(int limit, Instant now) {
        return jdbc.query("""
                SELECT id, to_address, subject, body, status, attempts
                FROM email_outbox
                WHERE status = 'PENDING' AND next_attempt_at <= ?
                ORDER BY created_at
                LIMIT ?
                FOR UPDATE SKIP LOCKED
                """,
                MAPPER, utc(now), limit);
    }

    @Override
    public void markSent(UUID id, Instant sentAt) {
        jdbc.update("""
                UPDATE email_outbox
                SET status = 'SENT', sent_at = ?, attempts = attempts + 1, last_error = NULL
                WHERE id = ?
                """,
                utc(sentAt), id);
    }

    @Override
    public void markRetry(UUID id, int attempts, Instant nextAttemptAt, String error) {
        jdbc.update("""
                UPDATE email_outbox
                SET attempts = ?, next_attempt_at = ?, last_error = ?
                WHERE id = ?
                """,
                attempts, utc(nextAttemptAt), error, id);
    }

    @Override
    public void markFailed(UUID id, int attempts, String error) {
        jdbc.update("""
                UPDATE email_outbox
                SET status = 'FAILED', attempts = ?, last_error = ?
                WHERE id = ?
                """,
                attempts, error, id);
    }

    private static OffsetDateTime utc(Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
