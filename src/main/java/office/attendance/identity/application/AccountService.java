package office.attendance.identity.application;

import office.attendance.identity.domain.Role;
import office.attendance.identity.domain.UserAccount;
import office.attendance.identity.domain.UserAccountRepository;
import office.attendance.shared.error.BadRequestException;
import office.attendance.shared.error.ConflictException;
import office.attendance.shared.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createEmployeeAccount(UUID employeeId, String email, String rawPassword) {
        if (repository.existsByEmployeeId(employeeId)) {
            throw new ConflictException("An account already exists for this employee");
        }
        create(employeeId, email, rawPassword, Role.EMPLOYEE);
    }

    @Transactional
    public void createAdmin(String email, String rawPassword) {
        create(null, email, rawPassword, Role.ADMIN);
    }

    @Transactional(readOnly = true)
    public boolean adminExists() {
        return repository.existsByRole(Role.ADMIN);
    }

    @Transactional
    public void deactivateForEmployee(UUID employeeId) {
        repository.deactivateByEmployeeId(employeeId);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        UserAccount account = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (!passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        repository.updatePasswordHash(userId, passwordEncoder.encode(newPassword));
    }
    @Transactional
    public void changeRole(String email, Role newRole, UUID actingUserId) {
        UserAccount target = repository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Account not found: " + email));

        if (target.getId().equals(actingUserId)) {
            throw new BadRequestException("You cannot change your own role");
        }
        if (target.getRole() == newRole) {
            return;
        }
        if (target.getRole() == Role.ADMIN && newRole != Role.ADMIN
                && repository.countActiveByRole(Role.ADMIN) <= 1) {
            throw new ConflictException("Cannot remove the last active admin");
        }
        repository.updateRole(target.getId(), newRole);
    }

    private void create(UUID employeeId, String email, String rawPassword, Role role) {
        String normalizedEmail = email.trim().toLowerCase();
        if (repository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email already in use: " + normalizedEmail);
        }
        repository.insert(UserAccount.create(
                employeeId, normalizedEmail, passwordEncoder.encode(rawPassword), role));
    }
}
