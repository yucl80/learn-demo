package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnusedLocalVariableCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                Set<String> usedVariables = new HashSet<>();
                Set<String> declaredVariables = new HashSet<>();

                // Collect declared variables
                method.findAll(VariableDeclarator.class).forEach(variable -> {
                    declaredVariables.add(variable.getNameAsString());
                });

                // Collect used variables
                method.findAll(ExpressionStmt.class).forEach(expressionStmt -> {
                    String expression = expressionStmt.toString();
                    declaredVariables.forEach(variable -> {
                        if (expression.contains(variable)) {
                            usedVariables.add(variable);
                        }
                    });
                });

                // Find unused variables
                declaredVariables.removeAll(usedVariables);
                if (!declaredVariables.isEmpty()) {
                    result.addIssue("Unused local variables in method: " + method.getNameAsString() + " at line " + method.getBegin().get().line + ": " + declaredVariables);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
