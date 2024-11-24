package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.formatter.CodeFormatter;
import org.eclipse.cdt.internal.formatter.CCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for formatting C/C++ source code using Eclipse CDT formatter
 */
public class FormatCode {
    private static final CodeFormatter formatter = new CCodeFormatter();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide the path to the source file as an argument");
            System.exit(1);
        }

        try {
            String inputFile = args[0];
            String formattedCode = formatFile(Paths.get(inputFile));
            System.out.println("Formatted code:");
            System.out.println(formattedCode);
        } catch (Exception e) {
            System.err.println("Error formatting code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Format the content of a C/C++ source file
     *
     * @param filePath Path to the source file
     * @return Formatted source code
     * @throws IOException If there's an error reading the file
     * @throws BadLocationException If there's an error during formatting
     */
    public static String formatFile(Path filePath) throws IOException, BadLocationException {
        try (var reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String sourceCode = Files.readString(filePath, StandardCharsets.UTF_8);
            return formatSourceCode(sourceCode);
        }
    }

    /**
     * Format the given C/C++ source code string
     *
     * @param sourceCode Source code to format
     * @return Formatted source code
     * @throws BadLocationException If there's an error during formatting
     */
    public static String formatSourceCode(String sourceCode) throws BadLocationException {
        // Configure formatting options if needed
        Map<String, String> options = new HashMap<>();
        
        TextEdit edit = formatter.format(
            CodeFormatter.K_TRANSLATION_UNIT,
            sourceCode,
            0,
            sourceCode.length(),
            0,
            null
        );

        if (edit == null) {
            throw new IllegalStateException("Formatting failed: No formatting changes were made");
        }

        IDocument document = new Document(sourceCode);
        edit.apply(document);
        return document.get();
    }
}
