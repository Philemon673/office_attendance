package office.attendance.roster.application;

import java.time.LocalTime;
import java.util.UUID;

public record ShiftAssignment(
        UUID shiftId,
        String shiftName,
        LocalTime startTime,
        LocalTime endTime,
        int graceMinutes) {
}