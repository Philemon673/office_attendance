package office.attendance.report.application;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record DailyEntry(
        UUID employeeId,
        String employeeNumber,
        String fullName,
        String shiftName,
        LocalTime shiftStart,
        LocalTime shiftEnd,
        Instant checkInAt,
        Instant checkOutAt,
        String recordStatus,
        int lateMinutes,
        Integer workedMinutes
) {
}
