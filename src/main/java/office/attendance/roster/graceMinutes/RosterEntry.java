package office.attendance.roster.domain;

import java.time.LocalDate;
import java.util.UUID;

public class RosterEntry {

    private final UUID id;
    private final UUID employeeId;
    private final UUID shiftId;
    private final LocalDate workDate;

    public RosterEntry(UUID id, UUID employeeId, UUID shiftId, LocalDate workDate) {
        this.id = id;
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.workDate = workDate;
    }

    public static RosterEntry create(UUID employeeId, UUID shiftId, LocalDate workDate) {
        return new RosterEntry(UUID.randomUUID(), employeeId, shiftId, workDate);
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public UUID getShiftId() { return shiftId; }
    public LocalDate getWorkDate() { return workDate; }
}