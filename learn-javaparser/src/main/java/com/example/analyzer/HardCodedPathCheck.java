package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class HardCodedPathCheck implements Check {

    private static final Pattern PATH_PATTERN = Pattern.compile(".*[\\/\\\\].*");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(StringLiteralExpr.class).forEach(stringLiteral -> {
                if (PATH_PATTERN.matcher(stringLiteral.getValue()).find()) {
                    result.addIssue("Hard-coded path detected: \"" + stringLiteral.getValue() + "\" at line " + stringLiteral.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
