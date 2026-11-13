package office.attendance.notification.application;

import office.attendance.notification.domain.EmailOutboxMessage;
import office.attendance.notification.domain.EmailOutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
@Component

public class OutboxProcessor {
    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final EmailOutboxRepository outbox;
    private final EmailSender sender;
    private final NotificationProperties properties;
    private final Clock clock;

    public OutboxProcessor(EmailOutboxRepository outbox,
                           EmailSender sender,
                           NotificationProperties properties,
                           Clock clock) {
        this.outbox = outbox;
        this.sender = sender;
        this.properties = properties;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${app.notification.poll-interval-ms:5000}")
    @Transactional
    public void process() {
        Instant now = clock.instant();
        List<EmailOutboxMessage> due = outbox.lockDue(properties.batchSize(), now);

        for (EmailOutboxMessage message : due) {
            try {
                sender.send(message.toAddress(), message.subject(), message.body());
                outbox.markSent(message.id(), now);
            } catch (Exception e) {
                int attempts = message.attempts() + 1;
                String error = truncate(e.getMessage());
                if (attempts >= properties.maxAttempts()) {
                    outbox.markFailed(message.id(), attempts, error);
                    log.error("Giving up on email {} after {} attempts: {}", message.id(), attempts, error);
                } else {
                    outbox.markRetry(message.id(), attempts, now.plus(backoff(attempts)), error);
                    log.warn("Email {} failed (attempt {}), will retry: {}", message.id(), attempts, error);
                }
            }
        }
    }

    private static Duration backoff(int attempts) {
        long seconds = Math.min(30L * (1L << Math.min(attempts - 1, 5)), 900L);
        return Duration.ofSeconds(seconds);
    }

    private static String truncate(String message) {
        if (message == null) {
            return "unknown error";
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }
}
