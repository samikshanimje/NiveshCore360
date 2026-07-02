package com.niveshcore360.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility exporter compiling report sheets into Tab-Separated Excel-compatible format.
 */
public class ExcelExporterUtil {

    public static void exportToExcel(String filePath, String[] headers, List<String[]> dataRows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write headers separated by tab characters
            for (int i = 0; i < headers.length; i++) {
                writer.write(headers[i]);
                if (i < headers.length - 1) {
                    writer.write("\t");
                }
            }
            writer.newLine();

            // Write rows
            for (String[] row : dataRows) {
                for (int i = 0; i < row.length; i++) {
                    writer.write(row[i] != null ? row[i] : "");
                    if (i < row.length - 1) {
                        writer.write("\t");
                    }
                }
                writer.newLine();
            }
        }
    }
}
