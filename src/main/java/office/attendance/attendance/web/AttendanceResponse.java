package office.attendance.attendance.web;

import office.attendance.attendance.domain.AttendanceRecord;
import office.attendance.attendance.domain.AttendanceStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
public record AttendanceResponse(
        UUID id,
        LocalDate workDate,
        Instant checkInAt,
        Instant checkOutAt,
        AttendanceStatus status,
        int lateMinutes,
        Integer workedMinutes) {

    public static AttendanceResponse from(AttendanceRecord r) {
        return new AttendanceResponse(
                r.getId(), r.getWorkDate(), r.getCheckInAt(), r.getCheckOutAt(),
                r.getStatus(), r.getLateMinutes(), r.getWorkedMinutes());
    }
}
