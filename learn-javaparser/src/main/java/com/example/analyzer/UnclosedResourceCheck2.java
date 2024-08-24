package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TryStmt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnclosedResourceCheck2 implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> resourceVariables = new HashSet<>();

            // Find resources in try-with-resources
            cu.findAll(TryStmt.class).forEach(tryStmt -> {
                tryStmt.getResources().forEach(resource -> {
                    if (resource.isVariableDeclarationExpr()) {
                        VariableDeclarationExpr varDecl = resource.asVariableDeclarationExpr();
                        varDecl.getVariables().forEach(var -> resourceVariables.add(var.getNameAsString()));
                    }
                });
            });

            // Find resources that are used but never closed outside of try-with-resources
            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (methodCall.getNameAsString().equals("close")) {
                    methodCall.getScope().ifPresent(scope -> {
                        if (scope.isNameExpr()) {
                            resourceVariables.remove(scope.asNameExpr().getNameAsString());
                        }
                    });
                }
            });

            for (String resource : resourceVariables) {
                result.addIssue("Resource " + resource + " may not be properly closed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
