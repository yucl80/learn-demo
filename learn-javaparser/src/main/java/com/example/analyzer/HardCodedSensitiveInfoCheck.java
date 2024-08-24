package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class HardCodedSensitiveInfoCheck implements Check {

    // 假设某些关键字表明硬编码的敏感信息
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile("password|secret|apikey|token", Pattern.CASE_INSENSITIVE);

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(StringLiteralExpr.class).forEach(literal -> {
                if (SENSITIVE_PATTERN.matcher(literal.getValue()).find()) {
                    result.addIssue("Hardcoded sensitive information found: \"" + literal.getValue() + "\" at line " + literal.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
