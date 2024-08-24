package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.TryStmt;

import java.io.File;
import java.io.IOException;

public class MemoryLeakCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            // Check for try-with-resources statements
            cu.findAll(TryStmt.class).forEach(tryStmt -> {
                if (!tryStmt.getResources().isEmpty()) {
                    result.addIssue("Potential memory leak detected: Try-with-resources statement found, ensure resources are properly managed at line " + tryStmt.getBegin().get().line);
                }
            });

            // Example: Check for large static collections that might not be cleared
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.isStatic() && method.getNameAsString().startsWith("get") && method.getType().isReferenceType()) {
                    result.addIssue("Potential memory leak: Static method returning a reference type at line " + method.getBegin().get().line + ". Ensure proper resource management.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
