package com.example.analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    public static void generateHtmlReport(List<String> issues, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("<html><body><h1>Static Analysis Report</h1><ul>");
            for (String issue : issues) {
                writer.write("<li>" + issue + "</li>");
            }
            writer.write("</ul></body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateJsonReport(List<String> issues, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("{\"issues\": [");
            for (int i = 0; i < issues.size(); i++) {
                writer.write("\"" + issues.get(i) + "\"");
                if (i < issues.size() - 1) {
                    writer.write(",");
                }
            }
            writer.write("]}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateCsvReport(List<String> issues, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            for (String issue : issues) {
                writer.write(issue + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
