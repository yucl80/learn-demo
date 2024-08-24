package com.example.analyzer;

import com.github.javaparser.ast.CompilationUnit;

public interface Check {
    void performCheck(CompilationUnit cu, CheckResult result);
}
