package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class InvalidLoggingCheck implements Check {

    private static final List<String> LOGGING_METHODS = Arrays.asList("debug", "info", "warn", "error");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (LOGGING_METHODS.contains(methodCall.getNameAsString())) {
                    // 检查日志方法是否使用了字符串连接或未正确使用占位符
                    if (methodCall.getArguments().size() == 1 && methodCall.getArguments().get(0).isBinaryExpr()) {
                        result.addIssue("Potential inefficient logging found at line " + methodCall.getBegin().get().line);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
