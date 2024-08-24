package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class HardCodedConfigValueCheck implements Check {

    private static final Pattern CONFIG_PATTERN = Pattern.compile("(?i)config|url|path|key");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(StringLiteralExpr.class).forEach(stringLiteral -> {
                if (CONFIG_PATTERN.matcher(stringLiteral.getValue()).find()) {
                    result.addIssue("Hard-coded configuration value detected: \"" + stringLiteral.getValue() + "\" at line " + stringLiteral.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
