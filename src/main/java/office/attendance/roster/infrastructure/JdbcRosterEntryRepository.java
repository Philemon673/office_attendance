package office.attendance.roster.infrastructure;

import office.attendance.roster.domain.RosterEntry;
import office.attendance.roster.domain.RosterEntryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcRosterEntryRepository implements RosterEntryRepository {
    private static final String COLUMNS = "id, employee_id, shift_id, work_date";

    private static final RowMapper<RosterEntry> MAPPER = (rs, i) -> new RosterEntry(
            rs.getObject("id", UUID.class),
            rs.getObject("employee_id", UUID.class),
            rs.getObject("shift_id", UUID.class),
            rs.getObject("work_date", LocalDate.class));

    private final JdbcTemplate jdbc;

    public JdbcRosterEntryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void assign(RosterEntry e) {
        jdbc.update("""
                INSERT INTO roster_entry (id, employee_id, shift_id, work_date)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (employee_id, work_date)
                DO UPDATE SET shift_id = EXCLUDED.shift_id
                """,
                e.getId(), e.getEmployeeId(), e.getShiftId(), e.getWorkDate());
    }

    @Override
    public Optional<RosterEntry> findByEmployeeAndDate(UUID employeeId, LocalDate date) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM roster_entry WHERE employee_id = ? AND work_date = ?",
                MAPPER, employeeId, date).stream().findFirst();
    }

    @Override
    public List<RosterEntry> findBetween(LocalDate from, LocalDate to) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM roster_entry WHERE work_date BETWEEN ? AND ? "
                        + "ORDER BY work_date, employee_id",
                MAPPER, from, to);
    }

    @Override
    public List<RosterEntry> findByEmployeeBetween(UUID employeeId, LocalDate from, LocalDate to) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM roster_entry WHERE employee_id = ? "
                        + "AND work_date BETWEEN ? AND ? ORDER BY work_date",
                MAPPER, employeeId, from, to);
    }
}
