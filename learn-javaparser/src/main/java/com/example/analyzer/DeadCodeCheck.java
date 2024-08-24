package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.io.File;
import java.io.IOException;

public class DeadCodeCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(ReturnStmt.class).forEach(returnStmt -> {
                returnStmt.getParentNode().ifPresent(parent -> {
                    for (Statement stmt : parent.getChildNodesByType(Statement.class)) {
                        if (stmt.equals(returnStmt)) {
                            break;
                        }
                        result.addIssue("Dead code found after return statement at line " + returnStmt.getBegin().get().line);
                    }
                });
            });

            cu.findAll(IfStmt.class).forEach(ifStmt -> {
                if (ifStmt.getCondition() instanceof BooleanLiteralExpr) {
                    boolean conditionValue = ((BooleanLiteralExpr) ifStmt.getCondition()).getValue();
                    if (!conditionValue && ifStmt.getThenStmt().isBlockStmt()) {
                        result.addIssue("Dead code found at line " + ifStmt.getThenStmt().getBegin().get().line);
                    } else if (conditionValue && ifStmt.getElseStmt().isPresent() && ifStmt.getElseStmt().get().isBlockStmt()) {
                        result.addIssue("Dead code found in else block at line " + ifStmt.getElseStmt().get().getBegin().get().line);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
