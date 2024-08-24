package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ResourceLeakCheck2 implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            Set<String> resourceMethods = new HashSet<>();
            Set<String> closeMethods = new HashSet<>();

            // Assuming resource methods are open/close methods for simplicity
            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();
                if (methodName.contains("open")) {
                    resourceMethods.add(methodName);
                } else if (methodName.contains("close")) {
                    closeMethods.add(methodName);
                }
            });

            resourceMethods.removeAll(closeMethods);

            if (!resourceMethods.isEmpty()) {
                result.addIssue("Potential resource leaks detected: " + resourceMethods);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
