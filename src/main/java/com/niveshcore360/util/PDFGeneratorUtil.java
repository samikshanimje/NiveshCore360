package com.niveshcore360.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Utility class utilizing OpenPDF to generate formatted PDF financial records.
 */
public class PDFGeneratorUtil {

    /**
     * Compiles a styled PDF statement matching the desktop visual theme.
     */
    public static void generateReportPDF(String filePath, String title, String subtitle, String[] headers, List<String[]> dataRows) 
            throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // 1. Title Section
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(79, 70, 229)); // Slate-indigo
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        // 2. Subtitle Section
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY);
        Paragraph subtitlePara = new Paragraph(subtitle + "\n\n", subtitleFont);
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitlePara);

        // 3. Data Table Setup
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Color headerColor = new Color(30, 41, 59); // Slate 800

        // Headers
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Data Rows
        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        boolean alternate = false;
        Color altColor = new Color(241, 245, 249); // Slate 100

        for (String[] row : dataRows) {
            for (String val : row) {
                PdfPCell cell = new PdfPCell(new Phrase(val, rowFont));
                cell.setPadding(6);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (alternate) {
                    cell.setBackgroundColor(altColor);
                }
                table.addCell(cell);
            }
            alternate = !alternate;
        }

        document.add(table);
        document.close();
    }
}
