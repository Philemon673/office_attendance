package office.attendance.notification.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EmailOutboxRepository {

    void insert(EmailOutboxMessage message);

    /** Locks and return pending  messages that are due so two instances never sent the same message **/

    List<EmailOutboxMessage> lockDue(int limit, Instant now);

    void markSent (UUID id);
    void markReceived(UUID id);

    void markFailed(UUID id, int attempts, String error);
}
