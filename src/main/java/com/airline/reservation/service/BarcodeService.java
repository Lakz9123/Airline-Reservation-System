package com.airline.reservation.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class BarcodeService {

    /**
     * Generate a Code128 barcode as PNG bytes.
     */
    public byte[] generateBarcodeBytes(String text, int width, int height) {
        try {
            Code128Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a Code128 barcode as a Base64-encoded data URI for embedding in HTML.
     */
    public String generateBarcodeBase64(String text, int width, int height) {
        byte[] bytes = generateBarcodeBytes(text, width, height);
        if (bytes == null) return null;
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Generate a QR Code as PNG bytes.
     */
    public byte[] generateQRCodeBytes(String text, int width, int height) {
        try {
            com.google.zxing.qrcode.QRCodeWriter writer = new com.google.zxing.qrcode.QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate a QR Code as a Base64-encoded data URI for embedding in HTML/PDF.
     */
    public String generateQRCodeBase64(String text, int width, int height) {
        byte[] bytes = generateQRCodeBytes(text, width, height);
        if (bytes == null) return null;
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }
}
