package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DeprecatedApiUsageCheck implements Check {

    private static final List<String> DEPRECATED_APIS = Arrays.asList(
            "java.util.Date", // Example deprecated API
            "java.util.Vector"
    );

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            // Check method calls
            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();
                if (DEPRECATED_APIS.stream().anyMatch(api -> methodCall.toString().contains(api))) {
                    result.addIssue("Deprecated API usage detected: " + methodName + " at line " + methodCall.getBegin().get().line);
                }
            });

            // Check class declarations
            cu.findAll(ClassOrInterfaceType.class).forEach(classType -> {
                if (DEPRECATED_APIS.contains(classType.getNameAsString())) {
                    result.addIssue("Deprecated class usage detected: " + classType.getNameAsString() + " at line " + classType.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
