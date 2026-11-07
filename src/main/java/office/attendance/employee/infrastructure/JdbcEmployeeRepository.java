package office.attendance.employee.infrastructure;

import office.attendance.employee.domain.Employee;
import office.attendance.employee.domain.EmployeeRespository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcEmployeeRepository implements EmployeeRespository {

    private static final RowMapper<Employee> MAPPER = (rs, i) -> new Employee(
            rs.getObject("id", UUID.class),
            rs.getString("employee_number"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getBoolean("active"));

    private final JdbcTemplate jdbc;

    public JdbcEmployeeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(Employee e) {
        jdbc.update("""
                INSERT INTO employee (id, employee_number, full_name, email, active)
                VALUES (?, ?, ?, ?, ?)
                """,
                e.getId(), e.getEmployeeNumber(), e.getFullName(), e.getEmail(), e.isActive());
    }

    @Override
    public int update(Employee e) {
        return jdbc.update("""
                UPDATE employee
                SET full_name = ?, email = ?, active = ?, updated_at = now()
                WHERE id = ?
                """,
                e.getFullName(), e.getEmail(), e.isActive(), e.getId());
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        return jdbc.query(
                "SELECT id, employee_number, full_name, email, active FROM employee WHERE id = ?",
                MAPPER, id).stream().findFirst();
    }

    @Override
    public List<Employee> findAll() {
        return jdbc.query(
                "SELECT id, employee_number, full_name, email, active FROM employee ORDER BY full_name",
                MAPPER);
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists("SELECT EXISTS (SELECT 1 FROM employee WHERE email = ?)", email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, UUID id) {
        return exists("SELECT EXISTS (SELECT 1 FROM employee WHERE email = ? AND id <> ?)", email, id);
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        return exists("SELECT EXISTS (SELECT 1 FROM employee WHERE employee_number = ?)", employeeNumber);
    }

    private boolean exists(String sql, Object... args) {
        return Boolean.TRUE.equals(jdbc.queryForObject(sql, Boolean.class, args));
    }
}