package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResourceLeakCheck implements Check {

    private static final List<String> CLOSABLE_METHODS = Arrays.asList("close", "release");

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodCallExpr.class).forEach(call -> {
                String methodName = call.getNameAsString();
                if (CLOSABLE_METHODS.contains(methodName)) {
                    // 检查是否在 try-with-resources 或 finally 中使用
                    if (!isWithinTryWithResourcesOrFinally(call)) {
                        result.addIssue("Resource not properly closed or released: " + methodName + " at line " + call.getBegin().get().line);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWithinTryWithResourcesOrFinally(MethodCallExpr call) {
        // 这个方法应该实现检查方法调用是否在 try-with-resources 或 finally 块内
        // 这里我们简化假设没有实现这个方法
        return false;
    }
}
