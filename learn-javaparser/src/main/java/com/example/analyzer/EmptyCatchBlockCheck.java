package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;

import java.io.File;
import java.io.IOException;

public class EmptyCatchBlockCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(CatchClause.class).forEach(catchClause -> {
                if (catchClause.getBody().isEmpty()) {
                    result.addIssue("Empty catch block found: " + catchClause);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
