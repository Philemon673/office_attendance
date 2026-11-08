package office.attendance.identity.application;

import office.attendance.identity.domain.Role;

import java.time.Instant;

public record LoginResult(String accessToken, Instant expiresAt, Role role) {
}
