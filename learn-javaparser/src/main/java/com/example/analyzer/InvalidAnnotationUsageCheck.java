package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class InvalidAnnotationUsageCheck implements Check {

    private static final Set<String> VALID_METHOD_ANNOTATIONS = Set.of("Override", "Deprecated");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                method.getAnnotations().forEach(annotation -> {
                    if (!VALID_METHOD_ANNOTATIONS.contains(annotation.getNameAsString())) {
                        result.addIssue("Invalid annotation usage: @" + annotation.getNameAsString() + " at line " + annotation.getBegin().get().line);
                    }
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
