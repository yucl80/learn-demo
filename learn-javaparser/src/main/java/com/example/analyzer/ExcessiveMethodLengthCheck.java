package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class ExcessiveMethodLengthCheck implements Check {

    private static final int MAX_METHOD_LINES = 50;

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int startLine = method.getBegin().map(pos -> pos.line).orElse(0);
                int endLine = method.getEnd().map(pos -> pos.line).orElse(0);
                int methodLength = endLine - startLine + 1;

                if (methodLength > MAX_METHOD_LINES) {
                    result.addIssue("Method exceeds the maximum allowed length (" + MAX_METHOD_LINES + " lines): "
                            + method.getNameAsString() + " has " + methodLength + " lines.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
