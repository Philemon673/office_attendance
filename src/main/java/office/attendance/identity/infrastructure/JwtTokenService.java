package office.attendance.identity.infrastructure;

import office.attendance.identity.application.IssuedToken;
import office.attendance.identity.application.TokenService;
import office.attendance.identity.domain.UserAccount;
import office.attendance.shared.config.JwtProperties;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
public class JwtTokenService implements TokenService {
    private final JwtEncoder encoder;
    private final Clock clock;
    private final JwtProperties properties;

    public JwtTokenService(JwtEncoder encoder, Clock clock, JwtProperties properties) {
        this.encoder = encoder;
        this.clock = clock;
        this.properties = properties;
    }

    @Override
    public IssuedToken issue(UserAccount account) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(Duration.ofMinutes(properties.expirationMinutes()));

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("attendance")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(account.getId().toString())
                .claim("role", account.getRole().name());
        if (account.getEmployeeId() != null) {
            claims.claim("employeeId", account.getEmployeeId().toString());
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new IssuedToken(token, expiresAt);
    }
}
