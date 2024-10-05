package com.yucl.learn.demo.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SqlInjectionChecker {

    // SQL 执行相关的类和方法映射
    private static final Map<String, Set<String>> methodMap = new HashMap<>();

    static {
        methodMap.put("java.sql.Statement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("java.sql.PreparedStatement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("org.springframework.jdbc.core.JdbcTemplate", Set.of("query", "update", "execute"));
        methodMap.put("org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate", Set.of("query", "update", "execute"));
        methodMap.put("org.springframework.data.jpa.repository.JpaRepository", Set.of("findAll", "findOne"));
    }

    public static void main(String[] args) throws IOException {
        // 创建 TypeSolver 来解析各类类型信息
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();

        // 1. 解析 JDK 类型
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // 2. 解析项目源码中的类型
        combinedTypeSolver.add(new JavaParserTypeSolver(new File("src")));

        // 3. 解析外部 JAR 包中的类型
        combinedTypeSolver.add(new JarTypeSolver(new File("lib/some-external-lib.jar")));
        combinedTypeSolver.add(new JarTypeSolver(new File("lib/another-lib.jar")));

        // 设置符号解析器
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setSymbolResolver(symbolSolver);
        JavaParser javaParser = new JavaParser(parserConfiguration);

        // 解析 Java 文件
        CompilationUnit cu = javaParser.parse(new File("path/to/your/SourceFile.java")).getResult().get();

        // 查找所有的 SQL 相关方法调用
        cu.findAll(MethodCallExpr.class).forEach(methodCall -> {
            try {
                // 获取方法调用的类型
                ResolvedType resolvedType = JavaParserFacade.get(combinedTypeSolver).getType(methodCall.getScope().orElse(null));
                if (resolvedType != null) {
                    String className = resolvedType.describe();
                    String methodName = methodCall.getNameAsString();

                    // 检查方法调用是否属于我们定义的 SQL 相关方法
                    if (isSqlMethod(className, methodName)) {
                        List<Expression> arguments = methodCall.getArguments();
                        if (!arguments.isEmpty()) {
                            Expression sqlArgument = arguments.get(0);

                            // 检查 SQL 语句是否是拼接的（直接或间接拼接）
                            if (isSqlInjectionRisk(sqlArgument, combinedTypeSolver)) {
                                System.out.println("Potential SQL Injection vulnerability detected in method call: " + methodCall);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not resolve type for method call: " + methodCall);
            }
        });
    }

    /**
     * 检查方法调用是否是 SQL 相关调用
     */
    private static boolean isSqlMethod(String className, String methodName) {
        return methodMap.containsKey(className) && methodMap.get(className).contains(methodName);
    }

    /**
     * 检查是否存在 SQL 注入风险，结合符号解析器分析变量来源。
     * 只标记拼接非静态字符串或变量的情况。
     */
    private static boolean isSqlInjectionRisk(Expression sqlExpression, CombinedTypeSolver combinedTypeSolver) {
        // 1. 检查是否是字符串拼接表达式，如使用 + 拼接
        if (sqlExpression.isBinaryExpr()) {
            BinaryExpr binaryExpr = sqlExpression.asBinaryExpr();
            if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
                return containsNonStaticPart(binaryExpr, combinedTypeSolver);
            }
        }

        // 2. 检查是否通过 StringBuilder, StringBuffer 进行拼接
        if (sqlExpression.isMethodCallExpr()) {
            MethodCallExpr methodCallExpr = sqlExpression.asMethodCallExpr();
            String methodName = methodCallExpr.getNameAsString();

            // StringBuilder.append() or StringBuffer.append()
            if (methodName.equals("append")) {
                return methodCallExpr.getArguments().stream()
                        .anyMatch(arg -> containsNonStaticPart(arg, combinedTypeSolver));
            }

            // String.format() 拼接 SQL
            if (methodName.equals("format")) {
                return methodCallExpr.getArguments().stream()
                        .anyMatch(arg -> containsNonStaticPart(arg, combinedTypeSolver));
            }
        }

        return false; // 如果没有发现拼接操作，则认为安全
    }

    /**
     * 检查表达式中是否包含非静态的部分，避免误报安全的静态拼接
     */
    private static boolean containsNonStaticPart(Expression expr, CombinedTypeSolver combinedTypeSolver) {
        if (expr.isStringLiteralExpr()) {
            // 如果是静态字符串，认为安全
            return false;
        } else if (expr.isNameExpr()) {
            // 如果是变量表达式，追踪其来源并判断是否为常量
            NameExpr nameExpr = expr.asNameExpr();
            try {
                ResolvedType resolvedType = JavaParserFacade.get(combinedTypeSolver).getType(nameExpr);
                // 可以进一步检查该变量的声明是否为常量
                return !resolvedType.isReferenceType(); // 简单检查变量是否为常量，真实情况需要更复杂的分析
            } catch (Exception e) {
                System.out.println("Could not resolve variable: " + nameExpr);
            }
        } else if (expr.isBinaryExpr()) {
            // 递归检查二元表达式的每一部分
            BinaryExpr binaryExpr = expr.asBinaryExpr();
            return containsNonStaticPart(binaryExpr.getLeft(), combinedTypeSolver) || containsNonStaticPart(binaryExpr.getRight(), combinedTypeSolver);
        }

        // 对于其他复杂情况，认为是非静态部分
        return true;
    }
}
