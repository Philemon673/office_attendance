package office.attendance.attendance.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.attendance")
public record AttendanceProperties(
        @DefaultValue("Africa/Douala") String zone,
        @DefaultValue("60") int checkInOpensMinutesBefore
) {
}
