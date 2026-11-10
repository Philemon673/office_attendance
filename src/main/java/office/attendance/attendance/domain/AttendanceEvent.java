package office.attendance.attendance.domain;

import java.time.Instant;
import java.util.UUID;

public record AttendanceEvent(
                              UUID id,
                              UUID employeeId,
                              AttendanceEventType type,
                              Instant occurredAt
) {
}
