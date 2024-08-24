package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.IfStmt;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DuplicateBranchCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(IfStmt.class).forEach(ifStmt -> {
                Set<String> branchSignatures = new HashSet<>();
                if (ifStmt.getElseStmt().isPresent()) {
                    String elseSignature = ifStmt.getElseStmt().get().toString();
                    if (branchSignatures.contains(elseSignature)) {
                        result.addIssue("Duplicate else branch found: " + elseSignature + " at line " + ifStmt.getBegin().get().line);
                    } else {
                        branchSignatures.add(elseSignature);
                    }
                }

                ifStmt.getChildNodes().forEach(branch -> {
                    String branchSignature = branch.toString();
                    if (branchSignatures.contains(branchSignature)) {
                        result.addIssue("Duplicate if branch found: " + branchSignature + " at line " + ifStmt.getBegin().get().line);
                    } else {
                        branchSignatures.add(branchSignature);
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
