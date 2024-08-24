package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;

public class NullPointerCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodCallExpr.class).forEach(call -> {
                if (call.getScope().isPresent()) {
                    if (call.getScope().get() instanceof NameExpr) {
                        NameExpr nameExpr = (NameExpr) call.getScope().get();
                        // 假设我们在上下文中不知道变量是否为 null，需要更高级的分析来检测
                        // 这里简单地标记所有可能的空指针调用
                        result.addIssue("Potential null pointer dereference: " + nameExpr.getNameAsString() + " at line " + call.getBegin().get().line);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
