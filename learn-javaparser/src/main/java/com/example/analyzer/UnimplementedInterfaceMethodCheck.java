package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnimplementedInterfaceMethodCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            

            Set<String> interfaceMethods = new HashSet<>();
            Set<String> implementedMethods = new HashSet<>();

            // Collect interface methods
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                clazz.getImplementedTypes().forEach(implementedType -> {
                    String interfaceName = implementedType.getNameAsString();
                    cu.findAll(ClassOrInterfaceDeclaration.class).forEach(iface -> {
                        if (iface.getNameAsString().equals(interfaceName)) {
                            iface.findAll(MethodDeclaration.class).forEach(method -> {
                                interfaceMethods.add(method.getNameAsString());
                            });
                        }
                    });
                });
            });

            // Collect implemented methods
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                clazz.findAll(MethodDeclaration.class).forEach(method -> {
                    implementedMethods.add(method.getNameAsString());
                });
            });

            // Find missing implementations
            interfaceMethods.removeAll(implementedMethods);
            if (!interfaceMethods.isEmpty()) {
                result.addIssue("Unimplemented interface methods detected: " + interfaceMethods);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
