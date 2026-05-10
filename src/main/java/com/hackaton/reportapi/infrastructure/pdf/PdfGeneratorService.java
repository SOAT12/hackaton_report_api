package com.hackaton.reportapi.infrastructure.pdf;

import com.hackaton.reportapi.domain.entity.Report;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGeneratorService {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - 2 * MARGIN;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generate(Report report) {
        try (var document = new PDDocument()) {
            var page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (var cs = new PDPageContentStream(document, page)) {
                float y = PDRectangle.A4.getHeight() - MARGIN;

                y = writeTitle(cs, report.getTitle(), y);
                y = writeMeta(cs, "Diagram ID: " + report.getDiagramId(), y - 6);
                y = writeMeta(cs, "Generated: " + report.getCreatedAt().format(DATE_FMT), y - 4);
                y -= 20;

                y = writeSection(cs, "Components", report.getReport().getComponents(), y);
                y = writeSection(cs, "Risks", report.getReport().getRisks(), y - 10);
                writeSection(cs, "Recommendations", report.getReport().getRecommendations(), y - 10);
            }

            var out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private float writeTitle(PDPageContentStream cs, String text, float y) throws IOException {
        var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        cs.beginText();
        cs.setFont(font, 20);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - 28;
    }

    private float writeMeta(PDPageContentStream cs, String text, float y) throws IOException {
        var font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        cs.beginText();
        cs.setFont(font, 10);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(text);
        cs.endText();
        return y - 14;
    }

    private float writeSection(PDPageContentStream cs, String title, List<String> items, float y) throws IOException {
        var bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        var regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        cs.beginText();
        cs.setFont(bold, 13);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(title);
        cs.endText();
        y -= 18;

        if (items != null) {
            for (String item : items) {
                cs.beginText();
                cs.setFont(regular, 11);
                cs.newLineAtOffset(MARGIN + 10, y);
                cs.showText("• " + item);
                cs.endText();
                y -= 16;
            }
        }
        return y;
    }
}
