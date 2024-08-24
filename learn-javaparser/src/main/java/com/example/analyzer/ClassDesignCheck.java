package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class ClassDesignCheck implements Check {

    @Override
    public void performCheck( CompilationUnit cu, CheckResult result) {
        try {


            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                // Check for large classes (more than 200 lines)
                if (clazz.getRange().isPresent() && clazz.getRange().get().getLineCount() > 200) {
                    result.addIssue("Class too large: " + clazz.getNameAsString() + " at line " + clazz.getBegin().get().line);
                }

                // Check for classes with too many methods (more than 20 methods)
                long methodCount = clazz.findAll(MethodDeclaration.class).size();
                if (methodCount > 20) {
                    result.addIssue("Class with too many methods: " + clazz.getNameAsString() + " at line " + clazz.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
