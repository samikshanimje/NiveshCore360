package com.niveshcore360.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class to export report structures into comma-separated value format.
 */
public class CSVExporterUtil {

    /**
     * Exports tabular data arrays to CSV.
     */
    public static void exportToCSV(String filePath, String[] headers, List<String[]> rows) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write Header
            for (int i = 0; i < headers.length; i++) {
                writer.append(escapeValue(headers[i]));
                if (i < headers.length - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");

            // Write Row Contents
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    writer.append(escapeValue(row[i]));
                    if (i < row.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        }
    }

    private static String escapeValue(String val) {
        if (val == null) return "";
        String escaped = val.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
