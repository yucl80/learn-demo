package com.yucl.code.search;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.JavaParser;

public class StructureIndexer {

    public String generateStructureIndex(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().get();
        StringBuilder structureIndex = new StringBuilder();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            structureIndex.append("Class: ").append(cls.getNameAsString()).append("\n");
            cls.findAll(MethodDeclaration.class).forEach(method -> {
                structureIndex.append("  Method: ").append(method.getNameAsString()).append("\n");
            });
        });

        return structureIndex.toString();
    }
}

