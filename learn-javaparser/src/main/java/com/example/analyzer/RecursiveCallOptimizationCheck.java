package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;

public class RecursiveCallOptimizationCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                String methodName = method.getNameAsString();
                method.findAll(MethodCallExpr.class).forEach(call -> {
                    if (call.getNameAsString().equals(methodName)) {
                        result.addIssue("Recursive call detected in method " + methodName + " at line " + call.getBegin().get().line
                                + ". Consider optimizing this with tail recursion or iterative approach.");
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
