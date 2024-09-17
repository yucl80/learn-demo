package com.yucl.learn.demo.jdt;

import org.eclipse.jdt.core.dom.*;
import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor extends ASTVisitor {
    // 存储解析到的方法调用信息
    private List<String> methodCalls = new ArrayList<>();

    public List<String> getMethodCalls() {
        return methodCalls;
    }


    // 在 visit 方法中检查反射调用
    @Override
    public boolean visit(MethodInvocation node) {
        if (isReflectionCall(node)) {
            if (!isReflectionCall(node)) { // 避免重复处理反射调用
                String methodName = node.getName().getIdentifier();
                List<?> typeArguments = node.typeArguments();
                if (!typeArguments.isEmpty()) {
                    methodCalls.add("Method: " + methodName + " with type arguments: " + typeArguments);
                }
            }
            return super.visit(node);
        } else {
            String methodName = node.getName().getIdentifier();
            String qualifier = node.getExpression() != null ? node.getExpression().toString() : "Unknown class";
            methodCalls.add("Method call: " + qualifier + "." + methodName);

            // 递归处理链式方法调用
            if (node.getExpression() instanceof MethodInvocation) {
                node.getExpression().accept(this);  // 递归处理链式调用
            }

            return super.visit(node);
        }
    }



    // Lambda 表达式
    @Override
    public boolean visit(LambdaExpression node) {
        methodCalls.add("Lambda expression");
        return super.visit(node);
    }

    // 匿名类
    @Override
    public boolean visit(ClassInstanceCreation node) {
        String className = node.getType().toString();
        methodCalls.add("Constructor call: " + className);

        // 检查是否是匿名类
        if (node.getAnonymousClassDeclaration() != null) {
            methodCalls.add("Anonymous class creation");
            node.getAnonymousClassDeclaration().accept(this);  // 递归处理匿名类中的方法
        }
        return super.visit(node);
    }

    // 构造函数调用
    @Override
    public boolean visit(ConstructorInvocation node) {
        methodCalls.add("Constructor invocation");
        return super.visit(node);
    }

    // 超类方法调用
    @Override
    public boolean visit(SuperMethodInvocation node) {
        String methodName = node.getName().getIdentifier();
        methodCalls.add("Super method call: " + methodName);
        return super.visit(node);
    }

    // 异常处理中的方法调用
    @Override
    public boolean visit(TryStatement node) {
        methodCalls.add("Method in try block");
        return super.visit(node);
    }

    @Override
    public boolean visit(CatchClause node) {
        methodCalls.add("Method in catch block for exception: " + node.getException().getType());
        return super.visit(node);
    }

//    @Override
//    public boolean visit(FinallyClause node) {
//        methodCalls.add("Method in finally block");
//        return super.visit(node);
//    }

    // 同步方法调用
    @Override
    public boolean visit(SynchronizedStatement node) {
        methodCalls.add("Method in synchronized block");
        return super.visit(node);
    }

    // 反射调用
    private boolean isReflectionCall(MethodInvocation node) {
        return "invoke".equals(node.getName().getIdentifier()) && node.getExpression() != null && node.getExpression().toString().contains("Method");
    }


    // 变量赋值中的方法调用
    @Override
    public boolean visit(VariableDeclarationFragment node) {
        if (node.getInitializer() instanceof MethodInvocation) {
            String methodName = ((MethodInvocation) node.getInitializer()).getName().getIdentifier();
            methodCalls.add("Variable assigned method result: " + methodName);
        }
        return super.visit(node);
    }

    // 方法引用 (:: 操作符)
//    @Override
//    public boolean visit(MethodReference node) {
//        String qualifier = node.getExpression() != null ? node.getExpression().toString() : "Unknown class";
//        String methodName = node.getName().getIdentifier();
//        methodCalls.add("Method reference: " + qualifier + "::" + methodName);
//        return super.visit(node);
//    }

    // 内部类和局部类方法调用
    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.isLocalTypeDeclaration()) {
            methodCalls.add("Local class: " + node.getName().getIdentifier());
        }
        return super.visit(node);
    }


    // 递归调用检测
    private String getEnclosingMethodOrLambda(ASTNode node) {
        ASTNode current = node;
        while (current != null) {
            if (current instanceof MethodDeclaration) {
                return ((MethodDeclaration) current).getName().getIdentifier();
            }
            if (current instanceof LambdaExpression) {
                return "LambdaExpression";
            }
            current = current.getParent();
        }
        return null;
    }

    // 检查递归调用
    public boolean visitRecursive(MethodInvocation node) {
        String methodName = node.getName().getIdentifier();
        String context = getEnclosingMethodOrLambda(node);
        if (methodName.equals(context)) {
            methodCalls.add("Recursive call: " + methodName);
        } else {
            methodCalls.add("Method: " + context + " calls " + methodName);
        }
        return super.visit(node);
    }

    // 输出捕获的所有方法调用
    public void printMethodCalls() {
        methodCalls.forEach(System.out::println);
    }

    public static void main(String[] args) {
        // 假设输入为解析后的 AST
        // CompilationUnit compilationUnit = ...;

        // 创建并使用 visitor
        MethodCallVisitor visitor = new MethodCallVisitor();
        // compilationUnit.accept(visitor);  // 让 AST 访问器访问解析的 AST

        // 输出解析结果
        visitor.printMethodCalls();
    }
}
