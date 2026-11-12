package office.attendance.notification.application;

import office.attendance.attendance.domain.AttendanceStatus;
import office.attendance.attendance.domain.CheckedInEvent;
import office.attendance.notification.domain.EmailOutboxMessage;
import office.attendance.notification.domain.EmailOutboxRepository;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class CheckInNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(CheckInNotificationListener.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ENGLISH);

    private final EmailOutboxRepository outbox;
    private final EmailTemplateRenderer renderer;
    private final NotificationProperties properties;
    private final ZoneId zone;

    public CheckInNotificationListener(
            EmailOutboxRepository outbox,
            EmailTemplateRenderer renderer,
            NotificationProperties properties
    ){
        this.outbox = outbox;
        this.renderer = renderer;
        this.properties = properties;
        this.zone = ZoneId.of(properties.zone());
    }
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCheckedIn(CheckedInEvent event) {
        try {
            ZonedDateTime local = event.checkedInAt().atZone(zone);
            boolean late = event.status() == AttendanceStatus.LATE;
            String time = TIME.format(local);

            Map<String, Object> model = Map.of(
                    "employeeName", event.employeeName(),
                    "shiftName", event.shiftName(),
                    "date", DATE.format(local),
                    "time", time,
                    "late", late,
                    "lateMinutes", event.lateMinutes());

            String employeeBody = renderer.render("email/check-in-employee", model);
            String employerBody = renderer.render("email/check-in-employer", model);

            outbox.insert(EmailOutboxMessage.pending(
                    event.employeeEmail(),
                    "Check-in confirmed at " + time,
                    employeeBody));

            String employerSubject = "Check-in: " + clean(event.employeeName()) + " at " + time
                    + (late ? " (late " + event.lateMinutes() + " min)" : "");
            for (String employer : properties.employerEmails()) {
                outbox.insert(EmailOutboxMessage.pending(employer, employerSubject, employerBody));
            }
        } catch (RuntimeException e) {
            // A notification problem must never block someone's check-in.
            log.error("Could not queue check-in emails for employee {}", event.employeeId(), e);
        }
    }

    private static String clean(String value) {
        return value.replaceAll("[\\r\\n]+", " ").trim();
    }
}
