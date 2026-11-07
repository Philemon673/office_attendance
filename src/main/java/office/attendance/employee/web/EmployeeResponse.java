package office.attendance.employee.web;

import office.attendance.employee.domain.Employee;

import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String employeeNumber,
        String fullName,
        String email,
        boolean active) {

    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(
                e.getId(), e.getEmployeeNumber(), e.getFullName(), e.getEmail(), e.isActive());
    }
}