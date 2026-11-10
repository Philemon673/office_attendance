package office.attendance.qr.infrastructure;

import office.attendance.qr.application.QrTokenService;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class StaticQrTokenService implements QrTokenService{

    private static final String MESSAGE = "office-attendance-qr-v1";

    private final String token;

    public StaticQrTokenService(QrProperties properties) {
        String secret = properties.secret();
        if (secret == null || secret.length() < 16) {
            throw new IllegalStateException(
                    "app.qr.secret must be at least 16 characters. Set the QR_SECRET environment variable.");
        }
        this.token = sign(secret);
    }

    @Override
    public String currentToken() {
        return token;
    }

    @Override
    public boolean isValid(String candidate) {
        if (candidate == null) {
            return false;
        }
        return MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                candidate.trim().getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(MESSAGE.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not sign QR token", e);
        }
    }
}
