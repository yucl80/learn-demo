package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class LongMethodCheck implements Check {
    private static final int MAX_METHOD_LENGTH = 50; // 可配置

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int length = method.getEnd().get().line - method.getBegin().get().line;
                if (length > MAX_METHOD_LENGTH) {
                    result.addIssue("Method " + method.getName() + " is too long: " + length + " lines");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
