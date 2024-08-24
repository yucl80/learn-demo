package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;

public class UnsafeDateTimeOperationCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (methodCall.getNameAsString().matches("setYear|setMonth|setDate")) {
                    result.addIssue("Unsafe date/time operation detected: " + methodCall.getNameAsString() + " at line " + methodCall.getBegin().get().line
                            + ". Consider using java.time package for date and time operations.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
