package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.AssignExpr;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SideEffectInLoopCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            checkLoopsForSideEffects(cu.findAll(ForStmt.class), result);
            checkLoopsForSideEffects(cu.findAll(WhileStmt.class), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLoopsForSideEffects(List<? extends NodeWithBody<?>> loops, CheckResult result) {
        for (NodeWithBody<?> loop : loops) {
            loop.getBody().findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (!methodCall.getNameAsString().equals("println")) { // example side effect check, except print
                    result.addIssue("Potential side effect found in loop at line " + methodCall.getBegin().get().line);
                }
            });

            loop.getBody().findAll(AssignExpr.class).forEach(assignExpr -> {
                result.addIssue("Assignment found in loop which may indicate side effect at line " + assignExpr.getBegin().get().line);
            });
        }
    }
}
