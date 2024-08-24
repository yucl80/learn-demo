package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnmodifiedVariableInLoopCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(ForStmt.class).forEach(forStmt -> {
                Set<String> loopVariables = new HashSet<>();
                forStmt.getBody().findAll(NameExpr.class).forEach(nameExpr -> {
                    loopVariables.add(nameExpr.getNameAsString());
                });

                // 检查变量是否在循环内修改
                boolean isModified = forStmt.getBody().findAll(NameExpr.class).stream().anyMatch(nameExpr ->
                        loopVariables.contains(nameExpr.getNameAsString()));

                if (!isModified) {
                    result.addIssue("Loop contains unmodified variable: " + forStmt);
                }
            });

            cu.findAll(WhileStmt.class).forEach(whileStmt -> {
                Set<String> loopVariables = new HashSet<>();
                whileStmt.getBody().findAll(NameExpr.class).forEach(nameExpr -> {
                    loopVariables.add(nameExpr.getNameAsString());
                });

                // 检查变量是否在循环内修改
                boolean isModified = whileStmt.getBody().findAll(NameExpr.class).stream().anyMatch(nameExpr ->
                        loopVariables.contains(nameExpr.getNameAsString()));

                if (!isModified) {
                    result.addIssue("Loop contains unmodified variable: " + whileStmt);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
