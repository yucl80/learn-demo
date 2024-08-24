package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.IfStmt;

import java.io.File;
import java.io.IOException;

public class EmptyIfBlockCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(IfStmt.class).forEach(ifStmt -> {
                if (ifStmt.getThenStmt().isBlockStmt() && ifStmt.getThenStmt().asBlockStmt().isEmpty()) {
                    result.addIssue("Empty if block found: " + ifStmt);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
