package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnusedReturnValueCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            Set<String> calledMethods = new HashSet<>();
            Set<String> usedMethods = new HashSet<>();

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                calledMethods.add(methodCall.getNameAsString());
            });

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.getType().isVoidType()) return;
                String methodName = method.getNameAsString();
                if (calledMethods.contains(methodName)) {
                    usedMethods.add(methodName);
                }
            });

            calledMethods.removeAll(usedMethods);

            if (!calledMethods.isEmpty()) {
                result.addIssue("Unused return values detected for methods: " + calledMethods);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
