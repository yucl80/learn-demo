package com.yucl.learn.demo.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaParserExample {
    public static void main(String[] args) throws IOException {
        // 读取Java文件
        FileInputStream in = new FileInputStream("D:\\workspaces\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\BizServiceImpl.java");
        CompilationUnit cu = StaticJavaParser.parse(in);

        // 获取类的import内容
        List<ImportDeclaration> imports = cu.getImports();
        System.out.println("Imports:");
        for (ImportDeclaration importDeclaration : imports) {
            System.out.println(importDeclaration.getName().toString());
        }

        String className = "BizServiceImpl";
        System.out.println(className);
        // 获取类范围内的变量，包括静态字段和常量
        List<ClassOrInterfaceDeclaration> classes = cu.getLocalDeclarationFromClassname(className);
        if (!classes.isEmpty()) {
            ClassOrInterfaceDeclaration classDecl = classes.get(0);
            System.out.println("\nVariables in the class:");
            for (FieldDeclaration field : classDecl.getFields()) {
                System.out.println(field.toString());

            }
            // 获取类范围内的方法及方法的完整签名
            System.out.println("\nMethods in the class:");
            for (MethodDeclaration method : classDecl.getMethods()) {
                String methodName = method.getName().toString();
                List<com.github.javaparser.ast.type.Type> parameterTypes = new ArrayList<>(method.getTypeParameters());
                com.github.javaparser.ast.type.Type returnType = method.getType();

                StringBuilder signatureBuilder = new StringBuilder();
                signatureBuilder.append(methodName).append(" (");
                for (int i = 0; i < parameterTypes.size(); i++) {
                    if (i > 0) {
                        signatureBuilder.append(", ");
                    }
                    signatureBuilder.append(parameterTypes.get(i).toString() + " " + method.getParameter(i).getNameAsString());
                }
                signatureBuilder.append(")");

                // Append return type if it's not void
                String returnTypeString = returnType.isVoidType() ? "void" : returnType.toString();
                signatureBuilder.append(" -> ").append(returnTypeString);

                System.out.println("Method: " + methodName);
                System.out.println("Signature: " + signatureBuilder.toString());
            }

        }

    }
}
