package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class BoardingPassPdfService {

    private final BarcodeService barcodeService;

    public BoardingPassPdfService(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Colour palette
    private static final Color PRIMARY    = new Color(13, 110, 253);
    private static final Color DARK_BG    = new Color(15, 23, 42);
    private static final Color ACCENT     = new Color(16, 185, 129);  // emerald-500
    private static final Color DIVIDER    = new Color(203, 213, 225);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color WHITE      = Color.WHITE;
    private static final Color AMBER      = new Color(255, 193, 7);

    public void generate(Booking booking, OutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        PdfContentByte canvas = writer.getDirectContent();
        float pageW = doc.getPageSize().getWidth();
        float pageH = doc.getPageSize().getHeight();

        // Background
        canvas.setColorFill(DARK_BG);
        canvas.rectangle(0, 0, pageW, pageH);
        canvas.fill();

        // Main card
        float margin = 40f;
        float cardW = pageW - margin * 2;
        float cardH = pageH - margin * 2;
        float cardX = margin;
        float cardY = margin;
        float cornerR = 16f;

        // Shadow
        canvas.setColorFill(new Color(0, 0, 0, 60));
        canvas.roundRectangle(cardX + 4, cardY - 4, cardW, cardH, cornerR);
        canvas.fill();

        // Card body
        canvas.setColorFill(WHITE);
        canvas.roundRectangle(cardX, cardY, cardW, cardH, cornerR);
        canvas.fill();

        // Left emerald stripe
        canvas.setColorFill(ACCENT);
        canvas.rectangle(cardX, cardY, 8, cardH);
        canvas.fill();

        // Top header
        float headerH = 60f;
        canvas.setColorFill(DARK_BG);
        canvas.rectangle(cardX + 8, cardY + cardH - headerH, cardW - 8, headerH);
        canvas.fill();
        canvas.roundRectangle(cardX, cardY + cardH - headerH, cardW, headerH, cornerR);
        canvas.fill();

        // Fonts
        BaseFont bf        = BaseFont.createFont(BaseFont.HELVETICA,        BaseFont.CP1252, false);
        BaseFont bfBold    = BaseFont.createFont(BaseFont.HELVETICA_BOLD,   BaseFont.CP1252, false);
        BaseFont bfOblique = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, false);

        float headerTop = cardY + cardH - headerH / 2f + 6f;

        // Header: Airline + BOARDING PASS
        canvas.beginText();
        canvas.setFontAndSize(bfBold, 20);
        canvas.setColorFill(WHITE);
        canvas.setTextMatrix(cardX + 30, headerTop);
        canvas.showText("✈  " + booking.getFlight().getAirline().getAirlineName());
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 12);
        canvas.setColorFill(ACCENT);
        canvas.setTextMatrix(cardX + 30, headerTop - 18);
        canvas.showText("BOARDING PASS");
        canvas.endText();

        // Booking ID top-right
        float bkIdX = cardX + cardW - 160;
        canvas.beginText();
        canvas.setFontAndSize(bf, 9);
        canvas.setColorFill(DIVIDER);
        canvas.setTextMatrix(bkIdX, headerTop);
        canvas.showText("BOOKING REFERENCE");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 15);
        canvas.setColorFill(AMBER);
        canvas.setTextMatrix(bkIdX, headerTop - 18);
        canvas.showText(String.format("SKY-%05d", booking.getId()));
        canvas.endText();

        // ── Route section ──
        float bodyTop = cardY + cardH - headerH - 30;
        String originCode = booking.getFlight().getOriginAirport().getAirportCode();
        String originCity = booking.getFlight().getOriginAirport().getCity();
        String destCode   = booking.getFlight().getDestinationAirport().getAirportCode();
        String destCity   = booking.getFlight().getDestinationAirport().getCity();

        float originX = cardX + 30;
        float arrowX  = cardX + cardW / 2f - 30;
        float destX   = cardX + cardW / 2f + 30;

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 42);
        canvas.setColorFill(DARK_BG);
        canvas.setTextMatrix(originX, bodyTop - 15);
        canvas.showText(originCode);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(originX, bodyTop - 32);
        canvas.showText(originCity);
        canvas.endText();

        // Arrow
        canvas.beginText();
        canvas.setFontAndSize(bfBold, 24);
        canvas.setColorFill(ACCENT);
        canvas.setTextMatrix(arrowX, bodyTop - 22);
        canvas.showText("------>");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 42);
        canvas.setColorFill(DARK_BG);
        canvas.setTextMatrix(destX, bodyTop - 15);
        canvas.showText(destCode);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(destX, bodyTop - 32);
        canvas.showText(destCity);
        canvas.endText();

        // ── Dashed tear-off ──
        float divY = bodyTop - 70;
        canvas.setColorStroke(DIVIDER);
        canvas.setLineDash(6, 4, 0);
        canvas.setLineWidth(1);
        canvas.moveTo(cardX + 16, divY);
        canvas.lineTo(cardX + cardW - 10, divY);
        canvas.stroke();
        canvas.setLineDash(0);

        // ── Details grid ──
        float detailTop = divY - 20;
        float col1 = cardX + 30;
        float col2 = cardX + 180;
        float col3 = cardX + 330;
        float col4 = cardX + 490;

        drawDetailBlock(canvas, bf, bfBold, col1, detailTop, "PASSENGER",
                booking.getUser().getName().toUpperCase());
        drawDetailBlock(canvas, bf, bfBold, col2, detailTop, "FLIGHT",
                booking.getFlight().getFlightNumber());
        drawDetailBlock(canvas, bf, bfBold, col3, detailTop, "DATE",
                booking.getFlight().getDepartureDateTime().format(DATE_FMT));
        drawDetailBlock(canvas, bf, bfBold, col4, detailTop, "CLASS",
                booking.getCabinClass() != null ? booking.getCabinClass().toUpperCase() : "ECONOMY");

        float detailTop2 = detailTop - 50;
        drawDetailBlock(canvas, bf, bfBold, col1, detailTop2, "SEAT(S)",
                booking.getSeatNumbers());
        drawDetailBlock(canvas, bf, bfBold, col2, detailTop2, "BOARDING TIME",
                booking.getFlight().getBoardingTime() != null
                        ? booking.getFlight().getBoardingTime().format(TIME_FMT) : "TBA");
        drawDetailBlock(canvas, bf, bfBold, col3, detailTop2, "GATE",
                booking.getFlight().getGateNumber() != null ? booking.getFlight().getGateNumber() : "TBA");
        drawDetailBlock(canvas, bf, bfBold, col4, detailTop2, "TERMINAL",
                booking.getFlight().getTerminal() != null ? booking.getFlight().getTerminal() : "TBA");

        float detailTop3 = detailTop2 - 50;
        drawDetailBlock(canvas, bf, bfBold, col1, detailTop3, "BOARDING ZONE",
                booking.getFlight().getBoardingZone() != null ? booking.getFlight().getBoardingZone() : "TBA");
        drawDetailBlock(canvas, bf, bfBold, col2, detailTop3, "DEPARTURE",
                booking.getFlight().getDepartureDateTime().format(TIME_FMT));
        drawDetailBlock(canvas, bf, bfBold, col3, detailTop3, "ARRIVAL",
                booking.getFlight().getArrivalDateTime().format(TIME_FMT));
        drawDetailBlock(canvas, bf, bfBold, col4, detailTop3, "AIRLINE",
                booking.getFlight().getAirline().getAirlineName());

        // ── Barcode (Code128) ──
        String barcodeText = String.format("SKY%05d", booking.getId());
        byte[] barcodeBytes = barcodeService.generateBarcodeBytes(barcodeText, 300, 50);
        if (barcodeBytes != null) {
            Image barcodeImage = Image.getInstance(barcodeBytes);
            barcodeImage.setAbsolutePosition(cardX + 30, cardY + 20);
            canvas.addImage(barcodeImage);
        }

        // Barcode text label
        canvas.beginText();
        canvas.setFontAndSize(bf, 9);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(cardX + 130, cardY + 10);
        canvas.showText(barcodeText);
        canvas.endText();

        // ── Footer ──
        canvas.beginText();
        canvas.setFontAndSize(bfOblique, 8);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(cardX + cardW - 350, cardY + 10);
        canvas.showText("Present this boarding pass at the gate. Keep this document until you have reached your destination.");
        canvas.endText();

        doc.close();
    }

    private void drawDetailBlock(PdfContentByte canvas,
                                 BaseFont label, BaseFont value,
                                 float x, float y,
                                 String labelText, String valueText) throws Exception {
        canvas.beginText();
        canvas.setFontAndSize(label, 8);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(x, y);
        canvas.showText(labelText);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(value, 13);
        canvas.setColorFill(DARK_BG);
        canvas.setTextMatrix(x, y - 16);
        canvas.showText(valueText != null ? valueText : "-");
        canvas.endText();
    }
}
