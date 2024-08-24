package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnnecessaryObjectCreationCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(ObjectCreationExpr.class).forEach(objectCreationExpr -> {
                // 假设检查常见的不必要的对象创建，如 `new String()`
                if (objectCreationExpr.getType().getNameAsString().equals("String") &&
                        objectCreationExpr.getArguments().isEmpty()) {
                    result.addIssue("Unnecessary object creation: " + objectCreationExpr.toString() + " at line " + objectCreationExpr.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void performCheck2(String codePath, CheckResult result) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));

            cu.findAll(ObjectCreationExpr.class).forEach(objectCreation -> {
                // Example check: If an object is created within a loop (potentially unnecessary creation)
                if (objectCreation.getParentNode().isPresent() && isWithinLoop(objectCreation.getParentNode().get())) {
                    result.addIssue("Unnecessary object creation within a loop at line " + objectCreation.getBegin().get().line);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWithinLoop(com.github.javaparser.ast.Node node) {
        if (node == null) return false;
        if (node instanceof com.github.javaparser.ast.stmt.ForStmt || node instanceof com.github.javaparser.ast.stmt.WhileStmt) {
            return true;
        }
        return node.getParentNode().map(this::isWithinLoop).orElse(false);
    }

    private static final Set<String> SINGLETON_CLASSES = new HashSet<>();
    static {
        SINGLETON_CLASSES.add("java.lang.String"); // Example of singleton usage
    }
    public void performCheck3(String codePath, CheckResult result) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));

            cu.findAll(ObjectCreationExpr.class).forEach(objectCreation -> {
                String className = objectCreation.getType().asString();
                if (SINGLETON_CLASSES.contains(className)) {
                    result.addIssue("Unnecessary object creation for a singleton class: " + className +
                            " at line " + objectCreation.getBegin().get().line +
                            ". Consider using a singleton instance.");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
