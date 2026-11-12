package office.attendance.notification.infrastructure;

import office.attendance.notification.application.EmailTemplateRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

public class ThymeleafEmailTemplateRenderer implements EmailTemplateRenderer {
    private final TemplateEngine engine;

    public ThymeleafEmailTemplateRenderer(TemplateEngine engine) {
        this.engine = engine;
    }

    @Override
    public String render(String template, Map<String, Object> model) {
        return engine.process(template, new Context(Locale.ENGLISH, model));
    }
}
