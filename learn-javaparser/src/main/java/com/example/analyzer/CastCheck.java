package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;

import java.io.File;
import java.io.IOException;

public class CastCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(CastExpr.class).forEach(castExpr -> {
                result.addIssue("Unchecked cast found: " + castExpr + " at line " + castExpr.getBegin().get().line);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
