package office.attendance.roster.web;

import office.attendance.roster.domain.RosterEntry;

import java.time.LocalDate;
import java.util.UUID;

public record RosterEntryResponse(
        UUID id,
        UUID employeeId,
        UUID shiftId,
        LocalDate workDate) {

    public static RosterEntryResponse from(RosterEntry e) {
        return new RosterEntryResponse(e.getId(), e.getEmployeeId(), e.getShiftId(), e.getWorkDate());
    }
}