package office.attendance.identity.web;

import office.attendance.identity.application.LoginResult;
import office.attendance.identity.domain.Role;

import java.time.Instant;

public record LoginResponse(String accessToken, String tokenType, Instant expiresAt, Role role) {

    public static LoginResponse from(LoginResult r) {
        return new LoginResponse(r.accessToken(), "Bearer", r.expiresAt(), r.role());
    }
}
