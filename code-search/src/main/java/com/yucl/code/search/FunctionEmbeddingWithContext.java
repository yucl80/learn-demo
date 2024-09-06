package com.yucl.code.search;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

/**
 * 在函数级别嵌入时，为了防止上下文缺失，可以扩展嵌入的内容，增加函数调用链、全局变量的定义等相关信息。以下是一个简单的实现思路：
 */

public class FunctionEmbeddingWithContext {

    // 获取函数调用链上下文
    private String getFunctionCallChainContext(MethodDeclaration method, CompilationUnit cu) {
        StringBuilder context = new StringBuilder();
        method.findAll(MethodCallExpr.class).forEach(call -> {
            String calledMethodName = call.getNameAsString();
            cu.findAll(MethodDeclaration.class).stream()
                    .filter(md -> md.getNameAsString().equals(calledMethodName))
                    .forEach(calledMethod -> context.append(calledMethod.toString()).append("\n"));
        });
        return context.toString();
    }

    // 获取全局变量上下文
    private String getGlobalVariableContext(MethodDeclaration method, CompilationUnit cu) {
        StringBuilder context = new StringBuilder();
        cu.findAll(VariableDeclarator.class).stream()
                .filter(var -> var.getParentNode().get() instanceof MethodDeclaration == false)
                .forEach(var -> context.append(var.toString()).append("\n"));
        return context.toString();
    }

    // 对函数进行上下文扩展并生成嵌入
    public String generateFunctionEmbeddingWithContext(MethodDeclaration method, CompilationUnit cu) {
        StringBuilder enrichedMethod = new StringBuilder();

        // 添加函数定义本身
        enrichedMethod.append(method.toString()).append("\n");

        // 添加函数调用链上下文
        enrichedMethod.append(getFunctionCallChainContext(method, cu));

        // 添加全局变量上下文
        enrichedMethod.append(getGlobalVariableContext(method, cu));

        // 调用嵌入生成方法（假设存在）
        // String embedding = generateEmbedding(enrichedMethod.toString());

        return enrichedMethod.toString(); // 返回上下文扩展后的函数表示（或嵌入）
    }
}
