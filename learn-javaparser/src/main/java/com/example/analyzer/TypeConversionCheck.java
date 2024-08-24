package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.CastExpr;

import java.io.File;
import java.io.IOException;

public class TypeConversionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(CastExpr.class).forEach(castExpr -> {
                TypeExpr typeExpr = castExpr.getExpression().asTypeExpr();
                String castType = typeExpr.toString();
                if (castType.equals("String") && castExpr.getExpression().toString().matches(".*[0-9].*")) {
                    result.addIssue("Potential invalid type conversion detected: " + castExpr.getExpression() + " to " + castType + " at line " + castExpr.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
