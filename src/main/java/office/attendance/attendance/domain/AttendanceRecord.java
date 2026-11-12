package office.attendance.attendance.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class AttendanceRecord {
    private final UUID id;
    private final UUID employeeId;
    private final LocalDate workDate;
    private final UUID shiftId;
    private final Instant checkInAt;
    private final Instant checkOutAt;      // null until checked out
    private final AttendanceStatus status;
    private final int lateMinutes;
    private final Integer workedMinutes;   // null until checked out
    public AttendanceRecord(UUID id, UUID employeeId, LocalDate workDate, UUID shiftId,
                            Instant checkInAt, Instant checkOutAt, AttendanceStatus status,
                            int lateMinutes, Integer workedMinutes) {
        this.id = id;
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.shiftId = shiftId;
        this.checkInAt = checkInAt;
        this.checkOutAt = checkOutAt;
        this.status = status;
        this.lateMinutes = lateMinutes;
        this.workedMinutes = workedMinutes;
    }

    public static AttendanceRecord checkIn(UUID employeeId, LocalDate workDate, UUID shiftId,
                                           Instant shiftStart, int graceMinutes, Instant checkInAt) {
        Instant lateAfter = shiftStart.plus( Duration.ofMinutes(graceMinutes));
        boolean late = checkInAt.isAfter(lateAfter);
        int lateMinutes = late ? (int) Duration.between(shiftStart, checkInAt).toMinutes() : 0;
        return new AttendanceRecord(UUID.randomUUID(), employeeId, workDate, shiftId, checkInAt, null,
                late ? AttendanceStatus.LATE : AttendanceStatus.ON_TIME, lateMinutes, null);
    }

    public AttendanceRecord checkedOut(Instant at) {
        int worked = (int) Math.max(0, Duration.between(checkInAt, at).toMinutes());
        return new AttendanceRecord(id, employeeId, workDate, shiftId, checkInAt, at,
                status, lateMinutes, worked);
    }

    public UUID getId() { return id; }
    public UUID getEmployeeId() { return employeeId; }
    public LocalDate getWorkDate() { return workDate; }
    public UUID getShiftId() { return shiftId; }
    public Instant getCheckInAt() { return checkInAt; }
    public Instant getCheckOutAt() { return checkOutAt; }
    public AttendanceStatus getStatus() { return status; }
    public int getLateMinutes() { return lateMinutes; }
    public Integer getWorkedMinutes() { return workedMinutes; }
}