package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RecursiveCallInLoopCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                String methodName = method.getNameAsString();
                checkLoopsForRecursion(methodName, method.findAll(ForStmt.class), result);
                checkLoopsForRecursion(methodName, method.findAll(WhileStmt.class), result);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLoopsForRecursion(String methodName, List<? extends NodeWithBody<?>> loops, CheckResult result) {
        for (NodeWithBody<?> loop : loops) {
            loop.getBody().findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (methodCall.getNameAsString().equals(methodName)) {
                    result.addIssue("Recursive call found in loop at line " + methodCall.getBegin().get().line);
                }
            });
        }
    }
}
