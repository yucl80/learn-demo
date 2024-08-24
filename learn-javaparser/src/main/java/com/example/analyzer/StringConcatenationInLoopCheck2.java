package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StringConcatenationInLoopCheck2 implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            checkLoops(cu.findAll(ForStmt.class), result);
            checkLoops(cu.findAll(WhileStmt.class), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLoops(List<? extends NodeWithBody<?>> loops, CheckResult result) {
        for (NodeWithBody<?> loop : loops) {
            loop.getBody().findAll(BinaryExpr.class).forEach(binaryExpr -> {
                if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS &&
                        (binaryExpr.getLeft() instanceof StringLiteralExpr || binaryExpr.getRight() instanceof StringLiteralExpr)) {
                    result.addIssue("String concatenation found in loop at line " + binaryExpr.getBegin().get().line);
                }
            });

            loop.getBody().findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
                if (methodCallExpr.getNameAsString().equals("concat")) {
                    result.addIssue("String concat method found in loop at line " + methodCallExpr.getBegin().get().line);
                }
            });
        }
    }
}
