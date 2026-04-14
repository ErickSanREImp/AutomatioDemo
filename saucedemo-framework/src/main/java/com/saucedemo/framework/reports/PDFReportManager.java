package com.saucedemo.framework.reports;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Single Responsibility (SOLID - S): Generates a PDF test-execution report using OpenPDF.
 */
public class PDFReportManager {

    private static final Logger log = LoggerFactory.getLogger(PDFReportManager.class);
    private static final String REPORT_DIR = "reports/pdf/";

    private static final Font FONT_TITLE   = new Font(Font.HELVETICA, 18, Font.BOLD,   new Color(33, 37, 41));
    private static final Font FONT_HEADING = new Font(Font.HELVETICA, 12, Font.BOLD,   new Color(52, 58, 64));
    private static final Font FONT_META    = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(73, 80, 87));
    private static final Font FONT_PASS    = new Font(Font.HELVETICA, 10, Font.BOLD,   new Color(25, 135, 84));
    private static final Font FONT_FAIL    = new Font(Font.HELVETICA, 10, Font.BOLD,   new Color(220, 53, 69));
    private static final Font FONT_SKIP    = new Font(Font.HELVETICA, 10, Font.BOLD,   new Color(255, 193, 7));
    private static final Font FONT_CELL    = new Font(Font.HELVETICA, 9,  Font.NORMAL, new Color(33, 37, 41));
    private static final Font FONT_HDR_CELL= new Font(Font.HELVETICA, 9,  Font.BOLD,   Color.WHITE);

    private static final Color COLOR_HDR_BG   = new Color(52, 58, 64);
    private static final Color COLOR_ROW_EVEN = new Color(248, 249, 250);
    private static final Color COLOR_ROW_ODD  = Color.WHITE;

    public void generateReport(List<TestResultInfo> results) {
        new File(REPORT_DIR).mkdirs();
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filePath = REPORT_DIR + "TestReport-" + timestamp + ".pdf";

        try (Document document = new Document(PageSize.A4.rotate())) {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addTitle(document, timestamp);
            addSummaryTable(document, results);
            document.add(Chunk.NEWLINE);
            addDetailTable(document, results);

            log.info("PDF report generated: {}", filePath);
        } catch (Exception e) {
            log.error("Failed to generate PDF report", e);
        }
    }

    private void addTitle(Document doc, String timestamp) throws DocumentException {
        Paragraph title = new Paragraph("SauceDemo – Test Execution Report", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph meta = new Paragraph("Generated: " + timestamp.replace("_", " "), FONT_META);
        meta.setAlignment(Element.ALIGN_CENTER);
        meta.setSpacingAfter(12f);
        doc.add(meta);
    }

    private void addSummaryTable(Document doc, List<TestResultInfo> results) throws DocumentException {
        long passed  = results.stream().filter(r -> "PASSED".equals(r.getStatus())).count();
        long failed  = results.stream().filter(r -> "FAILED".equals(r.getStatus())).count();
        long skipped = results.stream().filter(r -> "SKIPPED".equals(r.getStatus())).count();
        long total   = results.size();
        double passRate = total == 0 ? 0 : (passed * 100.0 / total);

        Paragraph heading = new Paragraph("Executive Summary", FONT_HEADING);
        heading.setSpacingBefore(8f);
        heading.setSpacingAfter(6f);
        doc.add(heading);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(60f);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidths(new float[]{20f, 20f, 20f, 20f, 20f});

        addHeaderCell(table, "Total");
        addHeaderCell(table, "Passed");
        addHeaderCell(table, "Failed");
        addHeaderCell(table, "Skipped");
        addHeaderCell(table, "Pass Rate");

        addSummaryCell(table, String.valueOf(total),  FONT_META);
        addSummaryCell(table, String.valueOf(passed),  FONT_PASS);
        addSummaryCell(table, String.valueOf(failed),  failed > 0 ? FONT_FAIL : FONT_META);
        addSummaryCell(table, String.valueOf(skipped), skipped > 0 ? FONT_SKIP : FONT_META);
        addSummaryCell(table, String.format("%.1f%%", passRate), passRate == 100 ? FONT_PASS : FONT_FAIL);

        doc.add(table);
    }

    private void addDetailTable(Document doc, List<TestResultInfo> results) throws DocumentException {
        Paragraph heading = new Paragraph("Test Results", FONT_HEADING);
        heading.setSpacingBefore(8f);
        heading.setSpacingAfter(6f);
        doc.add(heading);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{25f, 35f, 10f, 12f, 18f});

        addHeaderCell(table, "Class");
        addHeaderCell(table, "Test Name");
        addHeaderCell(table, "Status");
        addHeaderCell(table, "Duration (ms)");
        addHeaderCell(table, "Error");

        for (int i = 0; i < results.size(); i++) {
            TestResultInfo r = results.get(i);
            Color rowBg = (i % 2 == 0) ? COLOR_ROW_EVEN : COLOR_ROW_ODD;

            addDataCell(table, r.getClassName(),           FONT_CELL, rowBg);
            addDataCell(table, r.getTestName(),            FONT_CELL, rowBg);
            Font statusFont = "PASSED".equals(r.getStatus()) ? FONT_PASS
                            : "FAILED".equals(r.getStatus()) ? FONT_FAIL : FONT_SKIP;
            addDataCell(table, r.getStatus(),              statusFont, rowBg);
            addDataCell(table, String.valueOf(r.getDurationMs()), FONT_CELL, rowBg);
            String err = r.getErrorMessage() != null ? r.getErrorMessage() : "";
            if (err.length() > 120) err = err.substring(0, 120) + "…";
            addDataCell(table, err, FONT_CELL, rowBg);
        }

        doc.add(table);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HDR_CELL));
        cell.setBackgroundColor(COLOR_HDR_BG);
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addSummaryCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(5f);
        table.addCell(cell);
    }
}
