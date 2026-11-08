package office.attendance.identity.web;

import jakarta.validation.Valid;
import office.attendance.identity.application.AccountService;
import office.attendance.identity.application.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;

    public AuthController(AuthService authService, AccountService accountService) {
        this.authService = authService;
        this.accountService = accountService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return LoginResponse.from(authService.login(request.email(), request.password()));
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(
                UUID.fromString(jwt.getSubject()), request.currentPassword(), request.newPassword());
    }
}
