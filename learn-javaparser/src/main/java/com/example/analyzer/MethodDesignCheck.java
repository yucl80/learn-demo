package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class MethodDesignCheck implements Check {

    @Override
    public void performCheck( CompilationUnit cu, CheckResult result) {
        try {

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                // Check for methods that are too long (more than 50 lines)
                if (method.getBody().isPresent() && method.getBody().get().getRange().isPresent() &&
                        method.getBody().get().getRange().get().getLineCount() > 50) {
                    result.addIssue("Method too long: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                }

                // Check for methods with too many parameters (more than 5 parameters)
                if (method.getParameters().size() > 5) {
                    result.addIssue("Method with too many parameters: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
