package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MissingLoggingCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(method -> {
                BlockStmt body = method.getBody().orElse(null);
                if (body != null) {
                    List<String> statements = Arrays.asList( body.toString().split("\n"));
                    boolean containsLogStatement = statements.stream().anyMatch(stmt -> stmt.contains("logger") || stmt.contains("log"));
                    if (!containsLogStatement) {
                        result.addIssue("Missing logging statement in method: " + method.getNameAsString() + " at line " + method.getBegin().get().line);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
