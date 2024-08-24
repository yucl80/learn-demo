package com.example.analyzer;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AnalyzerConfig config = new AnalyzerConfig();
        AnalyzerLogger.log("Starting analysis...");

        String codePath = config.getProperty("code.path");
        String language = config.getProperty("code.language");
        String reportFormat = config.getProperty("output.format");
        String outputPath = config.getProperty("output.path");

        LanguageAdapter languageAdapter;
        if ("java".equalsIgnoreCase(language)) {
            languageAdapter = new JavaLanguageAdapter();
        } else {
            throw new UnsupportedOperationException("Unsupported language: " + language);
        }

        List<String> enabledChecks = Arrays.asList(config.getProperty("checks.enabled").split(","));
        CustomAnalyzer analyzer = new CustomAnalyzer(codePath, enabledChecks);
        CheckResult result = analyzer.runChecks();

        if (result.hasIssues()) {
            switch (reportFormat.toLowerCase()) {
                case "html":
                    ReportGenerator.generateHtmlReport(result.getIssues(), outputPath);
                    break;
                case "json":
                    ReportGenerator.generateJsonReport(result.getIssues(), outputPath);
                    break;
                case "csv":
                    ReportGenerator.generateCsvReport(result.getIssues(), outputPath);
                    break;
                default:
                    AnalyzerLogger.log("Unsupported report format: " + reportFormat);
            }
        }

        AnalyzerLogger.log("Analysis completed.");
    }
}
