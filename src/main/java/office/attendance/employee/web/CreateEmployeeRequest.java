package office.attendance.employee.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEmployeeRequest(
        @NotBlank @Size(max = 30) String employeeNumber,
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String temporaryPassword){
}