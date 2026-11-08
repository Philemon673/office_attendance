package office.attendance.identity.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository {
    void insert(UserAccount account);

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(UUID employeeId);

    boolean existsByRole(Role role);

    int updatePasswordHash(UUID id, String passwordHash);

    int deactivateByEmployeeId(UUID employeeId);

    int updateRole(UUID id, Role role);

    long countActiveByRole(Role role);
}
