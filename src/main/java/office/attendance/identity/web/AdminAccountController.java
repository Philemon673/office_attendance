package office.attendance.identity.web;
import jakarta.validation.Valid;
import office.attendance.identity.application.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/accounts")
public class AdminAccountController {

    private final AccountService accountService;

    public AdminAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeRole(@AuthenticationPrincipal Jwt jwt,
                           @Valid @RequestBody ChangeRoleRequest request) {
        accountService.changeRole(
                request.email(), request.role(), UUID.fromString(jwt.getSubject()));
    }
}
