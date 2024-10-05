package com.yucl.learn.demo.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;

import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;


import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MethodCallTypeResolverWithJars {
    public static void main(String[] args) throws IOException {
        // 创建 TypeSolver 来解析各类类型信息
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 1. 解析 JDK 类型
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 2. 解析项目源码中的类型
        combinedTypeSolver.add(new JavaParserTypeSolver(new File("src")));

        // 3. 解析外部 JAR 包中的类型
        // 假设项目使用的外部库位于 lib/ 目录下，可以加载多个 JAR 包
        combinedTypeSolver.add(new JarTypeSolver(new File("lib/some-external-lib.jar")));
        combinedTypeSolver.add(new JarTypeSolver(new File("lib/another-lib.jar")));

        // 设置符号解析器
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);
        JavaParser javaParser = new JavaParser(parserConfiguration);

        // 解析 Java 文件
        Optional<CompilationUnit> result = javaParser.parse(new File("path/to/your/SourceFile.java")).getResult();
        if (result.isPresent()) {
            CompilationUnit cu = result.get();
            // 查找所有的方法调用
            cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
                try {
                    // 解析方法调用的返回类型
                    ResolvedType returnType = JavaParserFacade.get(combinedTypeSolver).getType(methodCall);
                    System.out.println("Method call: " + methodCall + " returns type: " + returnType.describe());
                } catch (Exception e) {
                    System.out.println("Could not resolve type for method call: " + methodCall);
                }
            });
        }
    }
}
