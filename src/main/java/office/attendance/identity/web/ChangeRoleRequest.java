package office.attendance.identity.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import office.attendance.identity.domain.Role;

public record ChangeRoleRequest(
        @NotBlank @Email String email,
        @NotNull Role role) {
}

