package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class CyclomaticComplexityCheck implements Check {

    private static final int MAX_COMPLEXITY = 10;

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        {
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                int complexity = 1; // Start with 1 for the method itself
                List<Statement> statements = method.getBody().map(BlockStmt::getStatements).stream().flatMap(List::stream).collect(toList());
                for (Statement statement : statements) {
                    if (statement instanceof IfStmt || statement instanceof ForStmt || statement instanceof WhileStmt || statement instanceof DoStmt) {
                        complexity++;
                    }
                }
                if (complexity > MAX_COMPLEXITY) {
                    result.addIssue("High cyclomatic complexity detected in method: " + method.getNameAsString() + " at line " + method.getBegin().get().line + ". Complexity: " + complexity);
                }
            });

        }
    }
}
