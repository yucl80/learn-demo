package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;

public class InvalidAssertionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (methodCall.getNameAsString().equals("assert")) {
                    if (methodCall.getArguments().size() == 1 && methodCall.getArgument(0).isBooleanLiteralExpr()) {
                        BooleanLiteralExpr booleanLiteral = methodCall.getArgument(0).asBooleanLiteralExpr();
                        if (booleanLiteral.getValue()) {
                            result.addIssue("Invalid assertion: Assertion is always true at line " + methodCall.getBegin().get().line);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
