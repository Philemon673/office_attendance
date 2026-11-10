package office.attendance.qr.web;

import office.attendance.qr.application.QrImageRenderer;
import office.attendance.qr.application.QrTokenService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/qr")
public class QrController {

    private final QrTokenService tokenService;
    private final QrImageRenderer imageRenderer;

    public QrController(QrTokenService tokenService, QrImageRenderer imageRenderer) {
        this.tokenService = tokenService;
        this.imageRenderer = imageRenderer;
    }

    @GetMapping("/current")
    public QrResponse current() {
        return new QrResponse(tokenService.currentToken(), "STATIC");
    }

    @GetMapping(value = "/current.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> currentImage(@RequestParam(defaultValue = "400") int size) {
        int clamped = Math.max(128, Math.min(size, 1024));
        byte[] png = imageRenderer.renderPng(tokenService.currentToken(), clamped);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(png);
    }
}
