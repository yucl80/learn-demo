package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.IOException;

public class VariableNamingConventionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(VariableDeclarator.class).forEach(varDecl -> {
                String varName = varDecl.getNameAsString();
                if (!Character.isLowerCase(varName.charAt(0))) {
                    result.addIssue("Variable name does not follow naming convention (should start with a lowercase letter): " + varName + " at line " + varDecl.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
