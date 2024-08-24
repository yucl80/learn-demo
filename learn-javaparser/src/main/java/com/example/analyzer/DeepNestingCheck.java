package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.IOException;

public class DeepNestingCheck implements Check {

    private static final int MAX_NESTING_LEVEL = 3;

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(BlockStmt.class).forEach(block -> {
                int nestingLevel = getNestingLevel(block);
                if (nestingLevel > MAX_NESTING_LEVEL) {
                    result.addIssue("Code nesting too deep: " + nestingLevel + " levels at line " + block.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNestingLevel(Node node) {
        int level = 0;
        Node parent = node.getParentNode().orElse(null);
        while (parent != null) {
            if (parent instanceof BlockStmt) {
                level++;
            }
            parent = parent.getParentNode().orElse(null);
        }
        return level;
    }
}
