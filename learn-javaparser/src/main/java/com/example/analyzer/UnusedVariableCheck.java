package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnusedVariableCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            // 用于存储所有变量声明
            Map<String, VariableDeclarator> declaredVariables = new HashMap<>();
            // 用于存储所有变量使用
            Set<String> usedVariables = new HashSet<>();

            // 收集所有的变量声明
            cu.findAll(VariableDeclarator.class).forEach(variable -> {
                declaredVariables.put(variable.getNameAsString(), variable);
            });

            // 收集所有变量使用情况
            cu.findAll(NameExpr.class).forEach(nameExpr -> {
                usedVariables.add(nameExpr.getNameAsString());
            });

            // 检查未使用的变量
            declaredVariables.forEach((name, variable) -> {
                if (!usedVariables.contains(name)) {
                    System.out.println("Unused variable detected: " + name + " at line " + variable.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
