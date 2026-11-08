package office.attendance.roster.web;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AssignRosterRequest(
        @NotNull UUID employeeId,
        @NotNull UUID shiftId,
        @NotEmpty List<@NotNull LocalDate> dates) {
}