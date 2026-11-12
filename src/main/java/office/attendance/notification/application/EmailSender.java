package office.attendance.notification.application;

public interface EmailSender {
    void send(String to, String subject, String HtmlBody);
}
