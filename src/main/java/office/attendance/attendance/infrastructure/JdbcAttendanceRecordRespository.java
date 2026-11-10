package office.attendance.attendance.infrastructure;

import office.attendance.attendance.domain.AttendanceRecord;
import office.attendance.attendance.domain.AttendanceRecordRepository;
import office.attendance.attendance.domain.AttendanceStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcAttendanceRecordRespository implements AttendanceRecordRepository {

    private static final String COLUMNS = """
            id, employee_id, shift_id, work_date, check_in_at, check_out_at,
            status, late_minutes, worked_minutes
            """;

    private static final RowMapper<AttendanceRecord> MAPPER = (rs, i) -> new AttendanceRecord(
            rs.getObject("id", UUID.class),
            rs.getObject("employee_id", UUID.class),
            rs.getObject("work_date", LocalDate.class),
            rs.getObject("shift_id", UUID.class),
            instant(rs, "check_in_at"),
            instant(rs, "check_out_at"),
            AttendanceStatus.valueOf(rs.getString("status")),
            rs.getInt("late_minutes"),
            rs.getObject("worked_minutes", Integer.class));

    private final JdbcTemplate jdbc;

    public JdbcAttendanceRecordRespository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean insertIfAbsent(AttendanceRecord r) {
        int rows = jdbc.update("""
                INSERT INTO attendance_record
                    (id, employee_id, shift_id, work_date, check_in_at, status, late_minutes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (employee_id, work_date) DO NOTHING
                """,
                r.getId(), r.getEmployeeId(), r.getShiftId(), r.getWorkDate(),
                utc(r.getCheckInAt()), r.getStatus().name(), r.getLateMinutes());
        return rows == 1;
    }

    @Override
    public Optional<AttendanceRecord> findByEmployeeAndDate(UUID employeeId, LocalDate date) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM attendance_record WHERE employee_id = ? AND work_date = ?",
                MAPPER, employeeId, date).stream().findFirst();
    }

    @Override
    public boolean markCheckedOut(AttendanceRecord r) {
        int rows = jdbc.update("""
                UPDATE attendance_record
                SET check_out_at = ?, worked_minutes = ?, updated_at = now()
                WHERE id = ? AND check_out_at IS NULL
                """,
                utc(r.getCheckOutAt()), r.getWorkedMinutes(), r.getId());
        return rows == 1;
    }

    @Override
    public List<AttendanceRecord> findByEmployeeBetween(UUID employeeId, LocalDate from, LocalDate to) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM attendance_record WHERE employee_id = ? "
                        + "AND work_date BETWEEN ? AND ? ORDER BY work_date",
                MAPPER, employeeId, from, to);
    }

    private static OffsetDateTime utc(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant instant(ResultSet rs, String column) throws SQLException {
        OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.toInstant();
    }

}
