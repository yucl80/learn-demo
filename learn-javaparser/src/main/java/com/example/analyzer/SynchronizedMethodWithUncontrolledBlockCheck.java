package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.SynchronizedStmt;

import java.io.File;
import java.io.IOException;

public class SynchronizedMethodWithUncontrolledBlockCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(MethodDeclaration.class).forEach(methodDecl -> {
                if (methodDecl.isSynchronized()) {
                    methodDecl.getBody().ifPresent(body -> {
                        if (!body.findAll(SynchronizedStmt.class).isEmpty()) {
                            result.addIssue("Synchronized method contains another synchronized block at line " + methodDecl.getBegin().get().line);
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
