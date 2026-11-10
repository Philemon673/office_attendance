package office.attendance.qr.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.qr")
public record QrProperties(String secret) {
}
