package office.attendance.employee.web;

import jakarta.validation.Valid;
import office.attendance.employee.application.EmployeeService;
import office.attendance.employee.domain.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee created = service.create(
                request.employeeNumber(), request.fullName(), request.email(), request.temporaryPassword());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(EmployeeResponse.from(created));
    }

    @PostMapping("/{id}/account")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccount(@PathVariable UUID id, @Valid @RequestBody CreateAccountRequest request) {
        service.createAccount(id, request.temporaryPassword());
    }

    @GetMapping
    public List<EmployeeResponse> list() {
        return service.list().stream().map(EmployeeResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable UUID id) {
        return EmployeeResponse.from(service.get(id));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateEmployeeRequest request) {
        return EmployeeResponse.from(service.update(id, request.fullName(), request.email()));
    }

    @PatchMapping("/{id}/deactivate")
    public EmployeeResponse deactivate(@PathVariable UUID id) {
        return EmployeeResponse.from(service.deactivate(id));
    }
}