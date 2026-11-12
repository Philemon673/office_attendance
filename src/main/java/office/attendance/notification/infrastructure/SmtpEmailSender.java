package office.attendance.notification.infrastructure;

import jakarta.mail.MessagingException;
import office.attendance.notification.application.EmailSender;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final NotificationProperties properties;

    public SmtpEmailSender(JavaMailSender mailSender, NotificationProperties properties){
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public void send(String to, String subject, String HtmlBody){
        try{
            MimeMailMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper("message, false, UTF-8");
            helper.setFrom(properties.from());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new MailPreparationException("Could not prepare email to " + to, e);
        }
    }
}
