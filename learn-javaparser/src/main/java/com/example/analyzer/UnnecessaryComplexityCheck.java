package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;

import java.io.File;
import java.io.IOException;

public class UnnecessaryComplexityCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                method.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                    if (binaryExpr.getOperator().toString().matches("&&|\\|\\|") && binaryExpr.getLeft().toString().length() > 100) {
                        result.addIssue("Unnecessary complexity detected in method: " + method.getNameAsString() + " at line " + binaryExpr.getBegin().get().line);
                    }
                });

                method.findAll(ConditionalExpr.class).forEach(conditionalExpr -> {
                    if (conditionalExpr.getCondition().toString().length() > 100) {
                        result.addIssue("Unnecessary complexity detected in method: " + method.getNameAsString() + " at line " + conditionalExpr.getBegin().get().line);
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
