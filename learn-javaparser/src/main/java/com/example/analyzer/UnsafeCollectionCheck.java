package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UnsafeCollectionCheck implements Check {

    private static final List<String> UNSAFE_COLLECTIONS = Arrays.asList(
            "ArrayList", "HashMap", "HashSet", "LinkedList", "TreeMap", "TreeSet"
    );

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(ClassOrInterfaceType.class).forEach(type -> {
                if (UNSAFE_COLLECTIONS.contains(type.getNameAsString())) {
                    result.addIssue("Thread-unsafe collection usage found: " + type.getNameAsString() + " at line " + type.getBegin().get().line);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
