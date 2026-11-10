package office.attendance.attendance.domain;

import java.time.Instant;
import java.util.UUID;

public record CheckedInEvent(UUID recordId,
                             UUID employeeId,
                             String employeeName,
                             String employeeEmail,
                             String shiftName,
                             Instant checkedInAt,
                             AttendanceStatus status,
                             int lateMinutes) {
}
