package office.attendance.report.application;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record DailyReportRow(
        UUID employeeId,
        String employeeNumber,
        String fullName,
        String shiftName,
        LocalTime shiftStart,
        LocalTime shiftEnd,
        Instant checkInAt,
        Instant checkOutAt,
        DailyReportStatus status,
        int lateMinutes,
        Integer workedMinutes,
        boolean missingCheckout
) {
}
