package office.attendance.attendance.application;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AttendanceProperties.class)
public class AttendanceConfig {
}
