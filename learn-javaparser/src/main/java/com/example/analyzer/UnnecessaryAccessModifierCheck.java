package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;

public class UnnecessaryAccessModifierCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {


            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.getModifiers().contains(com.github.javaparser.ast.Modifier.Keyword.PUBLIC) &&
                        method.getParentNode().isPresent() &&
                        method.getParentNode().get() instanceof TypeDeclaration) {
                    TypeDeclaration<?> parentType = (TypeDeclaration<?>) method.getParentNode().get();
                    if (parentType.getModifiers().contains(Modifier.Keyword.PRIVATE)) {
                        result.addIssue("Unnecessary public access modifier detected for method: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
