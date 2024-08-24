package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class MissingJavadocCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.isPublic() && !method.hasJavaDocComment()) {
                    result.addIssue("Public method without Javadoc: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
