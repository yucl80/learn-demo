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
        cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
            try {
                ResolvedMethodDeclaration resolvedMethod = methodCall.resolve();

//                boolean isDeprecated = resolvedMethod.getAnnotations().stream()
//                        .anyMatch(a -> a.getName().getIdentifier().equals("Deprecated"));
//
//                if (isDeprecated) {
//                    result.addIssue("Usage of deprecated API: " +
//                            resolvedMethod.getQualifiedSignature() + " at line " +
//                            methodCall.getBegin().get().line);
//                }
            } catch (Exception e) {
//                result.addIssue("Failed to resolve method: " + methodCall.getNameAsString() +
//                        " at line " + methodCall.getBegin().get().line + ". Error: " + e.getMessage());
            }
        });
    }

}

