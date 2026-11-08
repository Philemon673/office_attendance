package office.attendance.roster.infrastructure;

import office.attendance.roster.domain.Shift;
import office.attendance.roster.domain.ShiftRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcShiftRepository implements ShiftRepository {
    private static final String COLUMNS = "id, name, start_time, end_time, grace_minutes";

    private static final RowMapper<Shift> MAPPER = (rs, i) -> new Shift(
            rs.getObject("id", UUID.class),
            rs.getString("name"),
            rs.getObject("start_time", LocalTime.class),
            rs.getObject("end_time", LocalTime.class),
            rs.getInt("grace_minutes"));

    private final JdbcTemplate jdbc;

    public JdbcShiftRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(Shift s) {
        jdbc.update("""
                INSERT INTO shift (id, name, start_time, end_time, grace_minutes)
                VALUES (?, ?, ?, ?, ?)
                """,
                s.getId(), s.getName(), s.getStartTime(), s.getEndTime(), s.getGraceMinutes());
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return jdbc.query("SELECT " + COLUMNS + " FROM shift WHERE id = ?", MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public List<Shift> findAll() {
        return jdbc.query("SELECT " + COLUMNS + " FROM shift ORDER BY start_time, name", MAPPER);
    }

    @Override
    public boolean existsByName(String name) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM shift WHERE name = ?)", Boolean.class, name));
    }
}
