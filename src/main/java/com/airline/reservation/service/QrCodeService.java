package com.airline.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    private static final Logger log = LoggerFactory.getLogger(QrCodeService.class);

    /**
     * Generates a QR code PNG and returns it as a full data URI:
     * "data:image/png;base64,<base64-bytes>"
     * Consistent with BarcodeService so templates can use th:src directly.
     * Returns null if generation fails (caller should guard with th:if).
     */
    public String generateQrCodeBase64(String text, int width, int height) {
        byte[] pngData = generateQrCodeBytes(text, width, height);
        if (pngData != null && pngData.length > 0) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);
        }
        return null;
    }

    public byte[] generateQrCodeBytes(String text, int width, int height) {
        if (text == null || text.isBlank()) {
            log.warn("QR generation skipped: text is null or blank");
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate QR code for text '{}': {}", text, e.getMessage(), e);
            return null;
        }
    }
}
