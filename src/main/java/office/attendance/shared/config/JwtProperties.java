package office.attendance.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.security.jwt")
public record JwtProperties
        (String secret, @DefaultValue("480") long expirationMinutes) {

}
