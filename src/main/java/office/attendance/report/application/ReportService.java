package office.attendance.report.application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    private static final DateTimeFormatter CSV_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);

    private final ReportQueryRepository queries;
    private final CsvExporter csv;
    private final Clock clock;
    private final ZoneId zone;

    public ReportService(ReportQueryRepository queries,
                         CsvExporter csv,
                         Clock clock,
                         @Value("${app.attendance.zone:Africa/Douala}") String zone) {
        this.queries = queries;
        this.csv = csv;
        this.clock = clock;
        this.zone = ZoneId.of(zone);
    }

    @Transactional(readOnly = true)
    public List<DailyReportRow> daily(LocalDate date) {
        Instant now = clock.instant();
        LocalDate today = LocalDate.ofInstant(now, zone);
        return queries.dailyEntries(date).stream()
                .map(entry -> toRow(entry, date, today, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MonthlyReportRow> monthly(YearMonth month) {
        LocalDate today = LocalDate.ofInstant(clock.instant(), zone);
        return queries.monthly(month.atDay(1), month.atEndOfMonth(), today);
    }

    @Transactional(readOnly = true)
    public String dailyCsv(LocalDate date) {
        List<List<String>> rows = daily(date).stream().map(r -> List.of(
                r.employeeNumber(),
                r.fullName(),
                r.shiftName(),
                r.shiftStart().toString(),
                r.shiftEnd().toString(),
                local(r.checkInAt()),
                local(r.checkOutAt()),
                r.status().name(),
                String.valueOf(r.lateMinutes()),
                r.workedMinutes() == null ? "" : String.valueOf(r.workedMinutes()),
                r.missingCheckout() ? "yes" : "no")).toList();

        return csv.toCsv(List.of("Employee number", "Name", "Shift", "Shift start", "Shift end",
                "Check-in", "Check-out", "Status", "Late minutes", "Worked minutes", "Missing check-out"), rows);
    }

    @Transactional(readOnly = true)
    public String monthlyCsv(YearMonth month) {
        List<List<String>> rows = monthly(month).stream().map(r -> List.of(
                r.employeeNumber(),
                r.fullName(),
                String.valueOf(r.rosteredDays()),
                String.valueOf(r.presentDays()),
                String.valueOf(r.onTimeDays()),
                String.valueOf(r.lateDays()),
                String.valueOf(r.absentDays()),
                String.valueOf(r.totalLateMinutes()),
                String.valueOf(r.totalWorkedMinutes()),
                String.format(Locale.ROOT, "%d:%02d",
                        r.totalWorkedMinutes() / 60, r.totalWorkedMinutes() % 60),
                String.valueOf(r.missingCheckouts()))).toList();

        return csv.toCsv(List.of("Employee number", "Name", "Rostered days", "Present days",
                "On time days", "Late days", "Absent days", "Total late minutes",
                "Total worked minutes", "Total worked (h:mm)", "Missing check-outs"), rows);
    }

    private DailyReportRow toRow(DailyEntry e, LocalDate date, LocalDate today, Instant now) {
        Instant shiftEnd = date.atTime(e.shiftEnd()).atZone(zone).toInstant();
        boolean hasRecord = e.checkInAt() != null;

        DailyReportStatus status;
        boolean missingCheckout = false;

        if (hasRecord) {
            boolean late = "LATE".equals(e.recordStatus()) || e.lateMinutes() > 0;
            status = late ? DailyReportStatus.LATE : DailyReportStatus.ON_TIME;
            missingCheckout = e.checkOutAt() == null && now.isAfter(shiftEnd);
        } else if (date.isAfter(today)) {
            status = DailyReportStatus.NOT_YET;
        } else {
            status = now.isAfter(shiftEnd) ? DailyReportStatus.ABSENT : DailyReportStatus.NOT_YET;
        }

        return new DailyReportRow(
                e.employeeId(), e.employeeNumber(), e.fullName(), e.shiftName(),
                e.shiftStart(), e.shiftEnd(), e.checkInAt(), e.checkOutAt(),
                status, e.lateMinutes(), e.workedMinutes(), missingCheckout);
    }

    private String local(Instant instant) {
        return instant == null ? "" : CSV_TIME.format(instant.atZone(zone));
    }
}
