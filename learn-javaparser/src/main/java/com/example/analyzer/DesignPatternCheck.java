package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.io.IOException;

public class DesignPatternCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            // Example check for Singleton pattern (no private constructor)
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                boolean hasPublicConstructor = clazz.getConstructors().stream().anyMatch(constructor -> constructor.isPublic());
                if (hasPublicConstructor) {
                    result.addIssue("Class may violate Singleton pattern: " + clazz.getNameAsString() + " at line " + clazz.getBegin().get().line);
                }
            });

            // Add more checks for other design patterns as needed

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
