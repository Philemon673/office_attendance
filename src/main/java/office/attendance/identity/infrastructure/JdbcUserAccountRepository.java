package office.attendance.identity.infrastructure;

import office.attendance.identity.domain.Role;
import office.attendance.identity.domain.UserAccount;
import office.attendance.identity.domain.UserAccountRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserAccountRepository implements UserAccountRepository {

    private static final String COLUMNS = "id, employee_id, email, password_hash, role, active";

    private static final RowMapper<UserAccount> MAPPER = (rs, i) -> new UserAccount(
            rs.getObject("id", UUID.class),
            rs.getObject("employee_id", UUID.class),
            rs.getString("email"),
            rs.getString("password_hash"),
            Role.valueOf(rs.getString("role")),
            rs.getBoolean("active"));

    private final JdbcTemplate jdbc;

    public JdbcUserAccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void insert(UserAccount a) {
        jdbc.update("""
                INSERT INTO user_account (id, employee_id, email, password_hash, role, active)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                a.getId(), a.getEmployeeId(), a.getEmail(), a.getPasswordHash(),
                a.getRole().name(), a.isActive());
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return jdbc.query("SELECT " + COLUMNS + " FROM user_account WHERE email = ?", MAPPER, email)
                .stream().findFirst();
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return jdbc.query("SELECT " + COLUMNS + " FROM user_account WHERE id = ?", MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists("SELECT EXISTS (SELECT 1 FROM user_account WHERE email = ?)", email);
    }

    @Override
    public boolean existsByEmployeeId(UUID employeeId) {
        return exists("SELECT EXISTS (SELECT 1 FROM user_account WHERE employee_id = ?)", employeeId);
    }

    @Override
    public boolean existsByRole(Role role) {
        return exists("SELECT EXISTS (SELECT 1 FROM user_account WHERE role = ?)", role.name());
    }

    @Override
    public int updatePasswordHash(UUID id, String passwordHash) {
        return jdbc.update(
                "UPDATE user_account SET password_hash = ?, updated_at = now() WHERE id = ?",
                passwordHash, id);
    }

    @Override
    public int deactivateByEmployeeId(UUID employeeId) {
        return jdbc.update(
                "UPDATE user_account SET active = FALSE, updated_at = now() WHERE employee_id = ?",
                employeeId);
    }

    //--------------------for promoting a user to admin-----------------//

    @Override
    public int updateRole(UUID id, Role role) {
        return jdbc.update(
                "UPDATE user_account SET role = ?, updated_at = now() WHERE id = ?",
                role.name(), id);
    }

    @Override
    public long countActiveByRole(Role role) {
        Long count = jdbc.queryForObject(
                "SELECT count(*) FROM user_account WHERE role = ? AND active = TRUE",
                Long.class, role.name());
        return count == null ? 0 : count;
    }

    private boolean exists(String sql, Object... args) {
        return Boolean.TRUE.equals(jdbc.queryForObject(sql, Boolean.class, args));
    }
}
