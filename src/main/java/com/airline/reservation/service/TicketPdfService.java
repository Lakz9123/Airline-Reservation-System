package com.airline.reservation.service;

import com.airline.reservation.entity.Booking;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class TicketPdfService {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FULL_FMT  = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    // ── Colour palette ──────────────────────────────────────────────────────
    private static final Color PRIMARY    = new Color(13,  110, 253);   // Bootstrap primary blue
    private static final Color DARK_BG    = new Color(15,  23,  42);    // deep navy
    private static final Color ACCENT     = new Color(255, 193,   7);   // amber
    private static final Color DIVIDER    = new Color(203, 213, 225);   // slate-300
    private static final Color TEXT_MUTED = new Color(100, 116, 139);   // slate-500
    private static final Color WHITE      = Color.WHITE;

    public void generate(Booking booking, OutputStream out) throws Exception {
        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        doc.open();

        PdfContentByte canvas = writer.getDirectContent();

        // ── Background ───────────────────────────────────────────────────────
        canvas.setColorFill(DARK_BG);
        canvas.rectangle(0, 0, doc.getPageSize().getWidth(), doc.getPageSize().getHeight());
        canvas.fill();

        // ── Main ticket card (white) ─────────────────────────────────────────
        float margin    = 40f;
        float cardW     = doc.getPageSize().getWidth()  - margin * 2;
        float cardH     = doc.getPageSize().getHeight() - margin * 2;
        float cardX     = margin;
        float cardY     = margin;
        float cornerR   = 16f;

        // Card shadow
        canvas.setColorFill(new Color(0, 0, 0, 60));
        canvas.roundRectangle(cardX + 4, cardY - 4, cardW, cardH, cornerR);
        canvas.fill();

        // Card body
        canvas.setColorFill(WHITE);
        canvas.roundRectangle(cardX, cardY, cardW, cardH, cornerR);
        canvas.fill();

        // Left colour stripe (primary blue)
        canvas.setColorFill(PRIMARY);
        canvas.rectangle(cardX, cardY, 8, cardH);
        canvas.fill();

        // Top header bar (dark navy)
        float headerH = 60f;
        canvas.setColorFill(DARK_BG);
        canvas.rectangle(cardX + 8, cardY + cardH - headerH, cardW - 8, headerH);
        canvas.fill();
        // Round top corners
        canvas.roundRectangle(cardX, cardY + cardH - headerH, cardW, headerH, cornerR);
        canvas.fill();

        // ── Fonts ─────────────────────────────────────────────────────────────
        BaseFont bf        = BaseFont.createFont(BaseFont.HELVETICA,        BaseFont.CP1252, false);
        BaseFont bfBold    = BaseFont.createFont(BaseFont.HELVETICA_BOLD,   BaseFont.CP1252, false);
        BaseFont bfOblique = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE,BaseFont.CP1252, false);

        // ── Header: airline name + BOARDING PASS ──────────────────────────────
        float headerTop = cardY + cardH - headerH / 2f + 6f;

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 20);
        canvas.setColorFill(WHITE);
        canvas.setTextMatrix(cardX + 30, headerTop);
        canvas.showText("✈  SkyFly Airlines");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bfOblique, 11);
        canvas.setColorFill(ACCENT);
        canvas.setTextMatrix(cardX + 30, headerTop - 18);
        canvas.showText("BOARDING PASS");
        canvas.endText();

        // Booking ID top-right
        canvas.beginText();
        canvas.setFontAndSize(bf, 9);
        canvas.setColorFill(DIVIDER);
        float bkIdX = cardX + cardW - 160;
        canvas.setTextMatrix(bkIdX, headerTop);
        canvas.showText("BOOKING REFERENCE");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bfBold, 15);
        canvas.setColorFill(ACCENT);
        canvas.setTextMatrix(bkIdX, headerTop - 18);
        canvas.showText(String.format("SKY-%05d", booking.getId()));
        canvas.endText();

        // ── Route section ────────────────────────────────────────────────────
        float bodyTop = cardY + cardH - headerH - 30;

        String origin = booking.getFlight().getOrigin().toUpperCase();
        String dest   = booking.getFlight().getDestination().toUpperCase();

        float originX = cardX + 30;
        float arrowX  = cardX + cardW / 2f - 30;
        float destX   = cardX + cardW / 2f + 30;

        // Origin city code (large)
        canvas.beginText();
        canvas.setFontAndSize(bfBold, 42);
        canvas.setColorFill(DARK_BG);
        canvas.setTextMatrix(originX, bodyTop - 15);
        String originCode = origin.length() >= 3 ? origin.substring(0, 3) : origin;
        canvas.showText(originCode);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(originX, bodyTop - 32);
        canvas.showText(origin);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(originX, bodyTop - 46);
        canvas.showText(booking.getFlight().getDepartureDateTime().format(TIME_FMT));
        canvas.endText();

        // Arrow + duration
        canvas.beginText();
        canvas.setFontAndSize(bfBold, 24);
        canvas.setColorFill(PRIMARY);
        canvas.setTextMatrix(arrowX, bodyTop - 22);
        canvas.showText("------>");
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 9);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(arrowX + 10, bodyTop - 38);
        canvas.showText(booking.getFlight().getDurationMinutes() + " min");
        canvas.endText();

        // Destination
        canvas.beginText();
        canvas.setFontAndSize(bfBold, 42);
        canvas.setColorFill(DARK_BG);
        canvas.setTextMatrix(destX, bodyTop - 15);
        String destCode = dest.length() >= 3 ? dest.substring(0, 3) : dest;
        canvas.showText(destCode);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(destX, bodyTop - 32);
        canvas.showText(dest);
        canvas.endText();

        canvas.beginText();
        canvas.setFontAndSize(bf, 10);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(destX, bodyTop - 46);
        canvas.showText(booking.getFlight().getArrivalDateTime().format(TIME_FMT));
        canvas.endText();

        // ── Dashed tear-off line ──────────────────────────────────────────────
        float divY = bodyTop - 80;
        canvas.setColorStroke(DIVIDER);
        canvas.setLineDash(6, 4, 0);
        canvas.setLineWidth(1);
        canvas.moveTo(cardX + 16, divY);
        canvas.lineTo(cardX + cardW - 10, divY);
        canvas.stroke();
        canvas.setLineDash(0);  // reset

        // Scissors icon
        canvas.beginText();
        canvas.setFontAndSize(bf, 11);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(cardX + cardW - 24, divY - 5);
        canvas.showText("✂");
        canvas.endText();

        // ── Details grid ─────────────────────────────────────────────────────
        float detailTop = divY - 20;
        float col1 = cardX + 30;
        float col2 = cardX + 180;
        float col3 = cardX + 340;
        float col4 = cardX + 490;

        drawDetailBlock(canvas, bf, bfBold, col1, detailTop, "PASSENGER",    booking.getUser().getName().toUpperCase());
        drawDetailBlock(canvas, bf, bfBold, col2, detailTop, "FLIGHT",       booking.getFlight().getFlightNumber());
        drawDetailBlock(canvas, bf, bfBold, col3, detailTop, "DATE",
                booking.getFlight().getDepartureDateTime().format(DATE_FMT));
        drawDetailBlock(canvas, bf, bfBold, col4, detailTop, "AIRLINE",      booking.getFlight().getAirlineName());

        float detailTop2 = detailTop - 50;
        drawDetailBlock(canvas, bf, bfBold, col1, detailTop2, "SEAT(S)",     booking.getSeatNumbers());
        drawDetailBlock(canvas, bf, bfBold, col2, detailTop2, "CLASS",       "ECONOMY");
        drawDetailBlock(canvas, bf, bfBold, col3, detailTop2, "STATUS",      booking.getStatus());
        drawDetailBlock(canvas, bf, bfBold, col4, detailTop2, "TOTAL FARE",
                String.format("$%.2f", booking.getTotalFare()));

        // ── Barcode-style decoration (purely decorative) ──────────────────────
        float barX = cardX + cardW - 95;
        float barY = cardY + 20;
        canvas.setColorFill(DARK_BG);
        int[] widths = {3,1,2,1,3,2,1,2,3,1,2,3,1,2,1,3,2,1,3,1,2,1,3,2,1};
        float bx = barX;
        for (int i = 0; i < widths.length; i++) {
            if (i % 2 == 0) {
                canvas.rectangle(bx, barY, widths[i], 60);
                canvas.fill();
            }
            bx += widths[i] + 1;
        }

        canvas.beginText();
        canvas.setFontAndSize(bf, 7);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(barX, barY - 10);
        canvas.showText(String.format("*SKY%05d*", booking.getId()));
        canvas.endText();

        // ── Footer ───────────────────────────────────────────────────────────
        canvas.beginText();
        canvas.setFontAndSize(bfOblique, 8);
        canvas.setColorFill(TEXT_MUTED);
        canvas.setTextMatrix(cardX + 30, cardY + 10);
        canvas.showText("This is an official SkyFly Airlines e-ticket. Present this document at the airport check-in counter.  " +
                "Booked on: " + booking.getBookingDate().format(FULL_FMT));
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
