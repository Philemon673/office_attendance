package office.attendance.notification.application;

import java.util.Map;

public interface EmailTemplateRenderer {
    String render(String template, Map<String, Object> model);
}
