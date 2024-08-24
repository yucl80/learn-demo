package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnclosedResourceCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> resourceVars = new HashSet<>();

            cu.findAll(VariableDeclarationExpr.class).forEach(varDecl -> {
                varDecl.getVariables().forEach(var -> {
                    if (var.getType().toString().matches(".*(File|InputStream|OutputStream|Reader|Writer).*")) {
                        resourceVars.add(var.getNameAsString());
                    }
                });
            });

            cu.findAll(TryStmt.class).forEach(tryStmt -> {
                tryStmt.getResources().forEach(resource -> resourceVars.remove(resource.toString()));
            });

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (resourceVars.contains(methodCall.getScope().map(Object::toString).orElse(""))) {
                    resourceVars.remove(methodCall.getScope().get().toString());
                }
            });

            for (String unclosedResource : resourceVars) {
                result.addIssue("Unclosed resource found: " + unclosedResource);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
