package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class NamingConventionCheck implements Check {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");
    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");


    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {


            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(type -> {
                if (!CLASS_NAME_PATTERN.matcher(type.getNameAsString()).matches()) {
                    result.addIssue("Class name does not follow naming convention: " + type.getNameAsString() + " at line " + type.getBegin().get().line);
                }
            });

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (!METHOD_NAME_PATTERN.matcher(method.getNameAsString()).matches()) {
                    result.addIssue("Method name does not follow naming convention: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                }
            });

            cu.findAll(FieldDeclaration.class).forEach(field -> {
                field.getVariables().forEach(variable -> {
                    if (!FIELD_NAME_PATTERN.matcher(variable.getNameAsString()).matches()) {
                        result.addIssue("Field name does not follow naming convention: " + variable.getNameAsString() + " at line " + variable.getBegin().get().line);
                    }
                });
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
