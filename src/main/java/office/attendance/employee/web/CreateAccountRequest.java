package office.attendance.employee.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank @Size(min = 8, max = 72) String temporaryPassword) {
}
