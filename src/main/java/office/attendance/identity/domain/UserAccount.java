package office.attendance.identity.domain;

import java.util.UUID;

public class UserAccount {
    private final UUID id;
    private final UUID employeeId;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final boolean active;


    public UserAccount(UUID id, UUID employeeId, String email, String passwordHash, Role role, boolean active) {
        this.id = id;
        this.employeeId = employeeId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
        this.role = role;
    }

    public static UserAccount create(UUID employeeId, String email, String passwordHash, Role role) {
        return new UserAccount(UUID.randomUUID(), employeeId, email, passwordHash, role, true);
    }

    public UUID getId() {
        return id;
    }
    public UUID getEmployeeId() {
        return employeeId;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public Role getRole() {
        return role;
    }
    public boolean isActive() {
        return active;
    }
}
