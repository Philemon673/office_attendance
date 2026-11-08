package office.attendance.roster.web;

import jakarta.validation.Valid;
import office.attendance.roster.application.ShiftService;
import office.attendance.roster.domain.Shift;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shifts")
public class ShiftController {

    private final ShiftService service;

    public ShiftController(ShiftService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShiftResponse> create(@Valid @RequestBody CreateShiftRequest request) {
        int grace = request.graceMinutes() == null ? 0 : request.graceMinutes();
        Shift created = service.create(request.name(), request.startTime(), request.endTime(), grace);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(ShiftResponse.from(created));
    }

    @GetMapping
    public List<ShiftResponse> list() {
        return service.list().stream().map(ShiftResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ShiftResponse get(@PathVariable UUID id) {
        return ShiftResponse.from(service.get(id));
    }
}