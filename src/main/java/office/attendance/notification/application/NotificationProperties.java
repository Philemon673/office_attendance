package office.attendance.notification.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties("app.notification")
public record NotificationProperties(
        @DefaultValue("attendance@localHost") String from,
        List<String> employerEmails,
        @DefaultValue("Africa/Douala") String zone,
        @DefaultValue("5") int maxAttempts,
        @DefaultValue("20") int batchSize
) {
    public NotificationProperties{
        employerEmails = employerEmails == null ? List.of() : List.copyOf(employerEmails);
    }
}
