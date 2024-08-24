package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InvalidNullCheckCheck implements Check {

    private static final Set<String> nonNullableVars = new HashSet<>();

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(NameExpr.class).forEach(nameExpr -> {
                if (nameExpr.getParentNode().isPresent() && nameExpr.getParentNode().get() instanceof BinaryExpr) {
                    BinaryExpr binaryExpr = (BinaryExpr) nameExpr.getParentNode().get();
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.NOT_EQUALS && binaryExpr.getRight().isNullLiteralExpr()) {
                        if (nonNullableVars.contains(nameExpr.getNameAsString())) {
                            result.addIssue("Invalid null check on non-nullable variable: " + nameExpr.getNameAsString() + " at line " + binaryExpr.getBegin().get().line);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
