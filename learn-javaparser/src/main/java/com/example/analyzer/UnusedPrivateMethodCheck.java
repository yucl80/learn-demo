package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnusedPrivateMethodCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            Set<String> methodNames = new HashSet<>();
            Set<String> usedMethods = new HashSet<>();

            // 收集所有的私有方法
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.isPrivate()) {
                    methodNames.add(method.getNameAsString());
                }
            });

            // 收集所有的调用方法
            cu.findAll(MethodCallExpr.class).forEach(call -> {
                usedMethods.add(call.getNameAsString());
            });

            // 找到未使用的私有方法
            methodNames.removeAll(usedMethods);
            for (String methodName : methodNames) {
                result.addIssue("Unused private method found: " + methodName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performCheck2(String codePath, CheckResult result) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));
            Set<String> privateMethods = new HashSet<>();
            Set<String> calledMethods = new HashSet<>();

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                if (method.isPrivate()) {
                    privateMethods.add(method.getNameAsString());
                }
            });

            cu.findAll(MethodCallExpr.class).forEach(call -> calledMethods.add(call.getNameAsString()));

            privateMethods.removeAll(calledMethods);

            for (String unusedMethod : privateMethods) {
                result.addIssue("Unused private method found: " + unusedMethod);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
