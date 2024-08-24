package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class UnsafeThreadOperationCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();

                // Check for thread unsafe methods like 'start', 'wait', 'notify'
                if (methodName.equals("start") || methodName.equals("wait") || methodName.equals("notify") || methodName.equals("notifyAll")) {
                    result.addIssue("Potentially unsafe thread operation: " + methodName + " at line " + methodCall.getBegin().get().line + ". Ensure proper synchronization.");
                }
            });

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.getNameAsString().equals("run") && method.getParameters().isEmpty()) {
                    result.addIssue("Potentially unsafe thread operation: 'run' method found at line " + method.getBegin().get().line + ". Ensure it is used correctly within a thread.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
