package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;

public class UnsafeTypeCastCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(CastExpr.class).forEach(castExpr -> {
                if (castExpr.getExpression().isMethodCallExpr()) {
                    MethodCallExpr methodCall = castExpr.getExpression().asMethodCallExpr();
                    if (methodCall.getTypeArguments().isPresent()) {
                        result.addIssue("Unsafe type cast detected for method call " + methodCall.getNameAsString() + " at line " + castExpr.getBegin().get().line);
                    }
                } else if (castExpr.getExpression().isNameExpr()) {
                    NameExpr nameExpr = castExpr.getExpression().asNameExpr();
                    // Simplified example: Flag all downcasts without instanceof checks
                    result.addIssue("Potential unsafe cast without instanceof check at line " + castExpr.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
