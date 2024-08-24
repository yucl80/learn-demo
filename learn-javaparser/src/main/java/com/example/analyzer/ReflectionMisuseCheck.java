package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;

public class ReflectionMisuseCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                String methodName = methodCall.getNameAsString();

                // Check for common reflection method usage
                if (methodName.equals("getDeclaredField") || methodName.equals("getDeclaredMethod") || methodName.equals("invoke")) {
                    result.addIssue("Potential reflection misuse: " + methodName + " at line " + methodCall.getBegin().get().line + ". Consider if reflection is truly necessary.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
