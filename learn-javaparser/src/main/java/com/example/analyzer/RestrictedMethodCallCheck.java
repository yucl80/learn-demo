package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RestrictedMethodCallCheck implements Check {

    private static final List<String> RESTRICTED_METHODS = Arrays.asList("System.exit", "Thread.sleep");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
                String methodName = methodCallExpr.getNameAsString();
                String fullMethodName = methodCallExpr.getScope().isPresent()
                        ? methodCallExpr.getScope().get().toString() + "." + methodName
                        : methodName;

                if (RESTRICTED_METHODS.contains(fullMethodName)) {
                    result.addIssue("Restricted method call found: " + fullMethodName + " at line " + methodCallExpr.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
