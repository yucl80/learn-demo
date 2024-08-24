package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.io.File;
import java.io.IOException;

public class StringConcatenationInLoopCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(ForStmt.class).forEach(forStmt -> {
                forStmt.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS &&
                            (binaryExpr.getLeft() instanceof StringLiteralExpr || binaryExpr.getRight() instanceof StringLiteralExpr)) {
                        result.addIssue("String concatenation in loop found: " + binaryExpr + " at line " + binaryExpr.getBegin().get().line);
                    }
                });
            });
            cu.findAll(WhileStmt.class).forEach(whileStmt -> {
                whileStmt.findAll(BinaryExpr.class).forEach(binaryExpr -> {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS &&
                            (binaryExpr.getLeft() instanceof StringLiteralExpr || binaryExpr.getRight() instanceof StringLiteralExpr)) {
                        result.addIssue("String concatenation in loop found: " + binaryExpr + " at line " + binaryExpr.getBegin().get().line);
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
