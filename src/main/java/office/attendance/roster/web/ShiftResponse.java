package office.attendance.roster.web;

import java.time.LocalTime;
import java.util.UUID;

import office.attendance.roster.domain.Shift;

public record ShiftResponse(
    UUID id,
    String name,
    LocalTime startTime,
    LocalTime endTime,
    int graceMinutes) {

        public static ShiftResponse from(Shift s) {
            return new ShiftResponse(
                    s.getId(), s.getName(), s.getStartTime(), s.getEndTime(), s.getGraceMinutes());
        }
}
