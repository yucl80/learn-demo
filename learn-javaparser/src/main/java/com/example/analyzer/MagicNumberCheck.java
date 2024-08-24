package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;

import java.io.File;
import java.io.IOException;

public class MagicNumberCheck implements Check {

    private static final int[] IGNORED_VALUES = {0, 1, -1}; // 忽略的一些常见值

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(IntegerLiteralExpr.class).forEach(intLiteral -> {
                int value = Integer.parseInt(intLiteral.getValue());
                boolean isIgnored = false;
                for (int ignoredValue : IGNORED_VALUES) {
                    if (value == ignoredValue) {
                        isIgnored = true;
                        break;
                    }
                }

                if (!isIgnored) {
                    result.addIssue("Magic number found: " + intLiteral + " at line " + intLiteral.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
