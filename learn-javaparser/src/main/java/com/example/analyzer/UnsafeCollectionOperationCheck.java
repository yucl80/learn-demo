package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnsafeCollectionOperationCheck implements Check {

    private static final Set<String> UNSAFE_COLLECTION_CLASSES = new HashSet<>();

    static {
        UNSAFE_COLLECTION_CLASSES.add("java.util.ArrayList");
        UNSAFE_COLLECTION_CLASSES.add("java.util.HashMap");
        // Add other non-thread-safe collections here
    }

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            cu.findAll(VariableDeclarationExpr.class).forEach(varDecl -> {
                if (varDecl.getElementType() instanceof ClassOrInterfaceType) {
                    ClassOrInterfaceType type = (ClassOrInterfaceType) varDecl.getElementType();
                    if (UNSAFE_COLLECTION_CLASSES.contains(type.getNameAsString())) {
                        result.addIssue("Unsafe collection type detected: " + type.getNameAsString() + " at line " + varDecl.getBegin().get().line +
                                ". Consider using a thread-safe collection in a multi-threaded context.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
