package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UninitializedVariableCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> declaredButUninitialized = new HashSet<>();

            cu.findAll(VariableDeclarator.class).forEach(var -> {
                if (!var.getInitializer().isPresent()) {
                    declaredButUninitialized.add(var.getNameAsString());
                }
            });

//            cu.findAll(NameExpr.class).forEach(nameExpr -> {
//                if (declaredButUninitialized.contains(nameExpr.getNameAsString())) {
//                    result.addIssue("Usage of uninitialized variable: " + nameExpr.getNameAsString() + " at line " + nameExpr.getBegin().get().line);
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
