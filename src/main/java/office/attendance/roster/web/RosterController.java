package office.attendance.roster.web;

import jakarta.validation.Valid;
import office.attendance.roster.application.RosterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rosters")
public class RosterController {

    private final RosterService service;

    public RosterController(RosterService service) {
        this.service = service;
    }

    @PutMapping
    public List<RosterEntryResponse> assign(@Valid @RequestBody AssignRosterRequest request) {
        return service.assign(request.employeeId(), request.shiftId(), request.dates()).stream()
                .map(RosterEntryResponse::from).toList();
    }

    @GetMapping
    public List<RosterEntryResponse> view(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.view(from, to).stream().map(RosterEntryResponse::from).toList();
    }

    @GetMapping("/employees/{employeeId}")
    public List<RosterEntryResponse> viewForEmployee(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.viewForEmployee(employeeId, from, to).stream()
                .map(RosterEntryResponse::from).toList();
    }
}
