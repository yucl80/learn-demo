package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class TooManyMethodParametersCheck implements Check {

    private static final int MAX_PARAMETERS = 5;

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.getParameters().size() > MAX_PARAMETERS) {
                    result.addIssue("Method with too many parameters detected: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
