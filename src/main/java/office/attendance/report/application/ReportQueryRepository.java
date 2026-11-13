package office.attendance.report.application;

import java.time.LocalDate;
import java.util.List;

public interface ReportQueryRepository {

    List<DailyEntry> dailyEntries(LocalDate date);

    /** today is passed in so past days without a record count as absent, and today does not. */
    List<MonthlyReportRow> monthly(LocalDate from, LocalDate to, LocalDate today);
}
