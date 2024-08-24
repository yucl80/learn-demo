package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.Modifier;

import java.io.File;
import java.io.IOException;

public class FieldAccessPermissionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(FieldDeclaration.class).forEach(field -> {
                boolean isPublic = field.getModifiers().contains(Modifier.publicModifier());
                boolean isPrivate = field.getModifiers().contains(Modifier.privateModifier());

                if (isPublic && !isPrivate) {
                    result.addIssue("Field with public access detected: " + field.getVariables().get(0).getNameAsString() + " at line " + field.getBegin().get().line + ". Consider using private access.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
