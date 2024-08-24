package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class PotentialMemoryLeakCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> instantiatedVariables = new HashSet<>();
            Set<String> neverClosedVariables = new HashSet<>();

            // Find instantiated objects
            cu.findAll(VariableDeclarator.class).forEach(varDecl -> {
                varDecl.getInitializer().ifPresent(init -> {
                    if (init.isObjectCreationExpr()) {
                        instantiatedVariables.add(varDecl.getNameAsString());
                    }
                });
            });

            // Find method calls to 'close'
            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (methodCall.getNameAsString().equals("close")) {
                    methodCall.getScope().ifPresent(scope -> {
                        if (scope.isNameExpr()) {
                            instantiatedVariables.remove(scope.asNameExpr().getNameAsString());
                        }
                    });
                }
            });

            neverClosedVariables.addAll(instantiatedVariables);

            for (String variable : neverClosedVariables) {
                result.addIssue("Potential memory leak: Object " + variable + " might not be closed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
