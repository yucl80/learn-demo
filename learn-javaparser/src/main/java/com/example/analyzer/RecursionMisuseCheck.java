package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RecursionMisuseCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> recursiveMethods = new HashSet<>();

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                String methodName = method.getNameAsString();
                method.findAll(MethodCallExpr.class).forEach(callExpr -> {
                    if (callExpr.getNameAsString().equals(methodName)) {
                        recursiveMethods.add(methodName);
                        result.addIssue("Potential recursion misuse: Method '" + methodName + "' calls itself at line " + callExpr.getBegin().get().line + ". Ensure termination conditions are met.");
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
