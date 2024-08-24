package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnusedImportsCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> importedClasses = new HashSet<>();
            Set<String> usedClasses = new HashSet<>();

            cu.findAll(ImportDeclaration.class).forEach(importDecl -> {
                String importedClass = importDecl.getNameAsString();
                importedClasses.add(importedClass.substring(importedClass.lastIndexOf('.') + 1));
            });

            cu.findAll(com.github.javaparser.ast.expr.NameExpr.class).forEach(nameExpr -> {
                usedClasses.add(nameExpr.getNameAsString());
            });

            importedClasses.removeAll(usedClasses);
            for (String unusedImport : importedClasses) {
                result.addIssue("Unused import found: " + unusedImport);
            }

            Set<String> imports = new HashSet<>();
            Set<String> usedTypes = new HashSet<>();
            cu.findAll(com.github.javaparser.ast.type.Type.class).forEach(type -> {
                usedTypes.add(type.asString());
            });


            // Find unused imports
            imports.removeAll(usedTypes);

            for (String unusedImport : imports) {
                result.addIssue("Unused import found: " + unusedImport);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
