package office.attendance.employee.application;

import office.attendance.employee.domain.Employee;
import office.attendance.employee.domain.EmployeeRespository;
import office.attendance.identity.application.AccountService;
import office.attendance.shared.error.ConflictException;
import office.attendance.shared.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRespository repository;
    private final AccountService accountService;

    public EmployeeService(EmployeeRespository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    @Transactional
    public Employee create(String employeeNumber, String fullName, String email, String temporaryPassword) {
        String normalizedEmail = email.trim().toLowerCase();
        String number = employeeNumber.trim();

        if (repository.existsByEmployeeNumber(number)) {
            throw new ConflictException("Employee number already in use: " + number);
        }
        if (repository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email already in use: " + normalizedEmail);
        }

        Employee employee = Employee.register(number, fullName.trim(), normalizedEmail);
        repository.insert(employee);
        accountService.createEmployeeAccount(employee.getId(), normalizedEmail, temporaryPassword);
        return employee;
    }

    /** For employees created before login existed. */
    @Transactional
    public void createAccount(UUID employeeId, String temporaryPassword) {
        Employee employee = get(employeeId);
        if (!employee.isActive()) {
            throw new ConflictException("Employee is inactive: " + employeeId);
        }
        accountService.createEmployeeAccount(employeeId, employee.getEmail(), temporaryPassword);
    }

    @Transactional
    public Employee update(UUID id, String fullName, String email) {
        Employee existing = get(id);
        String normalizedEmail = email.trim().toLowerCase();

        if (repository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new ConflictException("Email already in use: " + normalizedEmail);
        }

        Employee updated = existing.withDetails(fullName.trim(), normalizedEmail);
        repository.update(updated);
        return updated;
    }

    @Transactional
    public Employee deactivate(UUID id) {
        Employee deactivated = get(id).deactivated();
        repository.update(deactivated);
        accountService.deactivateForEmployee(id);
        return deactivated;
    }

    @Transactional(readOnly = true)
    public Employee get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Employee> list() {
        return repository.findAll();
    }
}