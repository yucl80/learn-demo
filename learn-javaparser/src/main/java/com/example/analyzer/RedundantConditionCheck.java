package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.stmt.IfStmt;

import java.io.File;
import java.io.IOException;

public class RedundantConditionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(IfStmt.class).forEach(ifStmt -> {
                if (ifStmt.getCondition() instanceof BooleanLiteralExpr) {
                    BooleanLiteralExpr condition = (BooleanLiteralExpr) ifStmt.getCondition();
                    if (condition.getValue()) {
                        result.addIssue("Redundant true condition in if-statement: " + ifStmt);
                    } else {
                        result.addIssue("Redundant false condition in if-statement: " + ifStmt);
                    }
                }
            });

            cu.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                if (binaryExpr.getOperator() == BinaryExpr.Operator.OR || binaryExpr.getOperator() == BinaryExpr.Operator.AND) {
                    if ((binaryExpr.getLeft() instanceof BooleanLiteralExpr) || (binaryExpr.getRight() instanceof BooleanLiteralExpr)) {
                        result.addIssue("Redundant condition found in expression: " + binaryExpr.toString() + " at line " + binaryExpr.getBegin().get().line);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
