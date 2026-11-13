package office.attendance.report.application;

import java.util.UUID;

public record MonthlyReportRow(
        UUID employeeId,
        String employeeNumber,
        String fullName,
        int rosteredDays,
        int presentDays,
        int onTimeDays,
        int lateDays,
        int absentDays,
        long totalLateMinutes,
        long totalWorkedMinutes,
        int missingCheckouts
) {
}
