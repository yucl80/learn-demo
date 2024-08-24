package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.io.File;
import java.io.IOException;

public class DeprecatedAPICheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                try {
                    ResolvedMethodDeclaration resolvedMethod = methodCall.resolve();
                    if (resolvedMethod.isDeprecated()) {
                        result.addIssue("Usage of deprecated API: " + resolvedMethod.getQualifiedSignature() + " at line " + methodCall.getBegin().get().line);
                    }
                } catch (Exception e) {
                    // Handle the resolution error gracefully
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
