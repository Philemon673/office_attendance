package office.attendance.attendance.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScanRequest(@NotBlank @Size(max = 200) String token) {
}
