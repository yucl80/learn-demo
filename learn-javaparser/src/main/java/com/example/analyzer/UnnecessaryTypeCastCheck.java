package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnnecessaryTypeCastCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> instanceOfTypes = new HashSet<>();

            // Collect types from instanceof checks
            cu.findAll(InstanceOfExpr.class).forEach(instanceOfExpr -> {
                instanceOfTypes.add(instanceOfExpr.getType().asString());
            });

            // Check for unnecessary casts
            cu.findAll(CastExpr.class).forEach(castExpr -> {
                if (instanceOfTypes.contains(castExpr.getType().asString())) {
                    result.addIssue("Unnecessary type cast found at line " + castExpr.getBegin().get().line);
                }
            });

            cu.findAll(CastExpr.class).forEach(castExpr -> {
                if (castExpr.getExpression().calculateResolvedType().equals(castExpr.getType())) {
                    result.addIssue("Unnecessary type cast detected: Casting to the same type at line " + castExpr.getBegin().get().line);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
