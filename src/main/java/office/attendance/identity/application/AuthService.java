package office.attendance.identity.application;


import office.attendance.identity.domain.UserAccount;
import office.attendance.identity.domain.UserAccountRepository;
import office.attendance.shared.error.UnauthorizedException;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {


    private static final String INVALID = "Invalid email or password";

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String dummyHash;

    public AuthService(UserAccountRepository repository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.dummyHash = passwordEncoder.encode("not-a-real-password");
    }

    public LoginResult login(String email, String password) {
        Optional<UserAccount> found = repository.findByEmail(email.trim().toLowerCase());

        // Always compare against a hash, so "unknown email" and "wrong password"
        // take about the same time and give the same answer.
        String hash = found.map(UserAccount::getPasswordHash).orElse(dummyHash);
        boolean passwordMatches = passwordEncoder.matches(password, hash);

        if (found.isEmpty() || !passwordMatches || !found.get().isActive()) {
            throw new UnauthorizedException(INVALID);
        }

        UserAccount account = found.get();
        IssuedToken token = tokenService.issue(account);
        return new LoginResult(token.value(), token.expiresAt(), account.getRole());
    }
}
