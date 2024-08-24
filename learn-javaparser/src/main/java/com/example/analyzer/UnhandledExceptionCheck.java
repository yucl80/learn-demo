package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.TryStmt;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UnhandledExceptionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                boolean hasTryCatch = method.findAll(TryStmt.class).stream()
                        .anyMatch(stmt -> stmt.getCatchClauses().isNonEmpty());

                if (!hasTryCatch) {
                    System.out.println("Potential unhandled exception in method: " + method.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performCheck2(String codePath, CheckResult result) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                List<TryStmt> tryStmts = method.findAll(TryStmt.class);

                // 假设所有抛出的异常都必须被捕获并处理（这是假设，实际需要更多逻辑来检查）
                if (tryStmts.isEmpty() && !method.getThrownExceptions().isEmpty()) {
                    result.addIssue("Method throws unchecked exception(s) without try-catch block: " + method.getNameAsString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
