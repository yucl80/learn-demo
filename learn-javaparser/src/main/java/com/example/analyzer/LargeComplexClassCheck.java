package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
import java.io.IOException;

public class LargeComplexClassCheck implements Check {

    private static final int MAX_CLASS_LENGTH = 200; // Max lines in a class
    private static final int MAX_METHOD_COUNT = 20;  // Max methods in a class

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                int classLength = clazz.getEnd().get().line - clazz.getBegin().get().line;
                int methodCount = clazz.getMethods().size();

                if (classLength > MAX_CLASS_LENGTH) {
                    result.addIssue("Class " + clazz.getNameAsString() + " is too large with " + classLength + " lines at line " + clazz.getBegin().get().line);
                }

                if (methodCount > MAX_METHOD_COUNT) {
                    result.addIssue("Class " + clazz.getNameAsString() + " has too many methods (" + methodCount + ") at line " + clazz.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
