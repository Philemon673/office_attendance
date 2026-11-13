package office.attendance.report.infrastructure;


import office.attendance.report.application.DailyEntry;
import office.attendance.report.application.MonthlyReportRow;
import office.attendance.report.application.ReportQueryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcReportQueryRepository implements ReportQueryRepository {

    private static final RowMapper<DailyEntry> DAILY = (rs, i) -> new DailyEntry(
            rs.getObject("employee_id", UUID.class),
            rs.getString("employee_number"),
            rs.getString("full_name"),
            rs.getString("shift_name"),
            rs.getObject("start_time", LocalTime.class),
            rs.getObject("end_time", LocalTime.class),
            instant(rs, "check_in_at"),
            instant(rs, "check_out_at"),
            rs.getString("status"),
            rs.getInt("late_minutes"),
            rs.getObject("worked_minutes", Integer.class));

    private static final RowMapper<MonthlyReportRow> MONTHLY = (rs, i) -> new MonthlyReportRow(
            rs.getObject("employee_id", UUID.class),
            rs.getString("employee_number"),
            rs.getString("full_name"),
            rs.getInt("rostered_days"),
            rs.getInt("present_days"),
            rs.getInt("on_time_days"),
            rs.getInt("late_days"),
            rs.getInt("absent_days"),
            rs.getLong("late_minutes"),
            rs.getLong("worked_minutes"),
            rs.getInt("missing_checkouts"));

    private final JdbcTemplate jdbc;

    public JdbcReportQueryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<DailyEntry> dailyEntries(LocalDate date) {
        return jdbc.query("""
                SELECT e.id AS employee_id, e.employee_number, e.full_name,
                       s.name AS shift_name, s.start_time, s.end_time,
                       r.check_in_at, r.check_out_at, r.status, r.late_minutes, r.worked_minutes
                FROM roster_entry re
                JOIN employee e ON e.id = re.employee_id
                JOIN shift s ON s.id = re.shift_id
                LEFT JOIN attendance_record r
                       ON r.employee_id = re.employee_id AND r.work_date = re.work_date
                WHERE re.work_date = ?
                ORDER BY e.full_name
                """,
                DAILY, date);
    }

    @Override
    public List<MonthlyReportRow> monthly(LocalDate from, LocalDate to, LocalDate today) {
        return jdbc.query("""
                SELECT e.id AS employee_id, e.employee_number, e.full_name,
                       count(*) AS rostered_days,
                       count(r.id) AS present_days,
                       count(*) FILTER (WHERE r.status = 'ON_TIME') AS on_time_days,
                       count(*) FILTER (WHERE r.status = 'LATE') AS late_days,
                       count(*) FILTER (WHERE r.id IS NULL AND re.work_date < ?) AS absent_days,
                       coalesce(sum(r.late_minutes), 0) AS late_minutes,
                       coalesce(sum(r.worked_minutes), 0) AS worked_minutes,
                       count(*) FILTER (WHERE r.id IS NOT NULL AND r.check_out_at IS NULL
                                        AND re.work_date < ?) AS missing_checkouts
                FROM roster_entry re
                JOIN employee e ON e.id = re.employee_id
                LEFT JOIN attendance_record r
                       ON r.employee_id = re.employee_id AND r.work_date = re.work_date
                WHERE re.work_date BETWEEN ? AND ?
                GROUP BY e.id, e.employee_number, e.full_name
                ORDER BY e.full_name
                """,
                MONTHLY, today, today, from, to);
    }

    private static Instant instant(ResultSet rs, String column) throws SQLException {
        OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.toInstant();
    }
}
