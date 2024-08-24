package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnusedPrivateFieldCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> privateFields = new HashSet<>();
            Set<String> usedFields = new HashSet<>();

            // Collect private fields
            cu.findAll(FieldDeclaration.class).forEach(fieldDecl -> {
                if (fieldDecl.isPrivate()) {
                    fieldDecl.getVariables().forEach(var -> privateFields.add(var.getNameAsString()));
                }
            });

            // Collect used fields
            cu.findAll(NameExpr.class).forEach(nameExpr -> {
                usedFields.add(nameExpr.getNameAsString());
            });

            // Find unused private fields
            privateFields.removeAll(usedFields);

            for (String unusedField : privateFields) {
                result.addIssue("Unused private field found: " + unusedField);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
