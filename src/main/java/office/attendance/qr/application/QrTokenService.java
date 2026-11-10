package office.attendance.qr.application;

public interface QrTokenService {
    /** The token currently encoded in the office QR code. */
    String currentToken();

    /** True if the scanned token is accepted. */
    boolean isValid(String token);
}
