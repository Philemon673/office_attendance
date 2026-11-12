package office.attendance.notification.domain;

import java.util.UUID;

public record EmailOutboxMessage(
        UUID id,
        String toAddress,
        String subject,
        String body,
        OutboxStatus status,
        int attempts
) {
    public static EmailOutboxMessage pending (String toAddress, String subject, String body) {
        return new EmailOutboxMessage(UUID.randomUUID(), toAddress,subject,body,OutboxStatus.PENDING,0);
    }


}
