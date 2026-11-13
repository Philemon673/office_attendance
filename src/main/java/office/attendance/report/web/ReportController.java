package office.attendance.report.web;


import office.attendance.report.application.DailyReportRow;
import office.attendance.report.application.MonthlyReportRow;
import office.attendance.report.application.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/daily")
    public List<DailyReportRow> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.daily(date);
    }

    @GetMapping("/monthly")
    public List<MonthlyReportRow> monthly(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return service.monthly(month);
    }

    @GetMapping("/daily/export")
    public ResponseEntity<byte[]> dailyExport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return csvResponse(service.dailyCsv(date), "attendance-daily-" + date + ".csv");
    }

    @GetMapping("/monthly/export")
    public ResponseEntity<byte[]> monthlyExport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return csvResponse(service.monthlyCsv(month), "attendance-monthly-" + month + ".csv");
    }

    private ResponseEntity<byte[]> csvResponse(String csv, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
