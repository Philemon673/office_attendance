package office.attendance.attendance.infrastructure;

import office.attendance.attendance.domain.AttendanceEvent;
import office.attendance.attendance.domain.AttendanceEventRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Repository
public class JdbcAttendanceEventRepository implements AttendanceEventRepository {

    private final JdbcTemplate jdbc;

    public JdbcAttendanceEventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(AttendanceEvent e) {
        jdbc.update("""
                INSERT INTO attendance_event (id, employee_id, type, occurred_at)
                VALUES (?, ?, ?, ?)
                """,
                e.id(), e.employeeId(), e.type().name(),
                OffsetDateTime.ofInstant(e.occurredAt(), ZoneOffset.UTC));
    }
}
