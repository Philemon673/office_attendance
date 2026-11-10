package office.attendance.attendance.web;

import jakarta.validation.Valid;
import office.attendance.attendance.application.AttendanceService;
import office.attendance.shared.error.ForbiddenException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse checkIn(@AuthenticationPrincipal Jwt jwt,
                                      @Valid @RequestBody ScanRequest request) {
        return AttendanceResponse.from(service.checkIn(employeeId(jwt), request.token()));
    }

    @PostMapping("/check-out")
    public AttendanceResponse checkOut(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody ScanRequest request) {
        return AttendanceResponse.from(service.checkOut(employeeId(jwt), request.token()));
    }

    @GetMapping("/me")
    public List<AttendanceResponse> mine(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.myRecords(employeeId(jwt), from, to).stream()
                .map(AttendanceResponse::from).toList();
    }

    /** The employee comes from the login token, never from the request body. */
    private UUID employeeId(Jwt jwt) {
        String id = jwt.getClaimAsString("employeeId");
        if (id == null) {
            throw new ForbiddenException("This account is not linked to an employee");
        }
        return UUID.fromString(id);
    }
}
