package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.io.File;
import java.io.IOException;

public class InvariantConditionInLoopCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(ForStmt.class).forEach(forStmt -> {
                if (forStmt.getCompare().isPresent() && forStmt.getCompare().get() instanceof BooleanLiteralExpr) {
                    result.addIssue("Invariant condition found in 'for' loop at line " + forStmt.getBegin().get().line);
                }
            });

            cu.findAll(WhileStmt.class).forEach(whileStmt -> {
                if (whileStmt.getCondition() instanceof BooleanLiteralExpr) {
                    result.addIssue("Invariant condition found in 'while' loop at line " + whileStmt.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
