package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;

public class ExcessiveLinesCheck implements Check {

    private static final int MAX_LINES = 1000;

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            int lineCount = cu.getEnd().map(pos -> pos.line).orElse(0);

            if (lineCount > MAX_LINES) {
                result.addIssue("File exceeds the maximum allowed lines (" + MAX_LINES + "): " + lineCount + " lines found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
