package office.attendance.qr.application;

public interface QrImageRenderer {
    /** Renders the content as a PNG QR code of the given pixel size. */
    byte[] renderPng(String content, int size);
}
