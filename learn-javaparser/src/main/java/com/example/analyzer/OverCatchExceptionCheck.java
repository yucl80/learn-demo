package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;

public class OverCatchExceptionCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(CatchClause.class).forEach(catchClause -> {
                ClassOrInterfaceType exceptionType = (ClassOrInterfaceType) catchClause.getParameter().getType();
                if ("Exception".equals(exceptionType.getNameAsString()) || "Throwable".equals(exceptionType.getNameAsString())) {
                    result.addIssue("Overly generic exception caught: " + exceptionType + " in catch block: " + catchClause);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
