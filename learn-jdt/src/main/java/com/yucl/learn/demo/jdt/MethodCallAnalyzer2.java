package com.yucl.learn.demo.jdt;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IMethodBinding;
import java.util.*;

public class MethodCallAnalyzer2 {

    private Map<String, List<String>> callGraph = new HashMap<>();
    private String currentClassName;
    private String currentMethodName;
    private Set<IMethodBinding> visitedMethods = new HashSet<>();

    public static void main(String[] args) {
        ASTParser parser = createASTParser("Example.java"); // 替换为实际的Java文件路径
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        new MethodCallAnalyzer2().analyze(cu);
    }

    private static ASTParser createASTParser(String fileName) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(fileName.toCharArray());
        parser.setResolveBindings(true);
        return parser;
    }

    public void analyze(CompilationUnit cu) {
        cu.accept(new MethodCallVisitor());
        printCallGraph();
    }

    class MethodCallVisitor extends ASTVisitor {

        @Override
        public boolean visit(TypeDeclaration typeDeclaration) {
            currentClassName = typeDeclaration.getName().getIdentifier();
            super.visit(typeDeclaration);
            currentClassName = null;
            return true;
        }

        @Override
        public boolean visit(MethodDeclaration methodDeclaration) {
            IMethodBinding methodBinding = methodDeclaration.resolveBinding();
            if (methodBinding != null && !visitedMethods.contains(methodBinding)) {
                visitedMethods.add(methodBinding);
                String methodName = methodBinding.getName();
                String methodKey = currentClassName + "." + methodName
                        + Arrays.toString(methodBinding.getParameterTypes());
                currentMethodName = methodKey;
                // super.visitMethodDeclaration(methodDeclaration);
                super.visit(methodDeclaration);
                currentMethodName = null;
            }
            return true;
        }

        // ... 其他重写的方法 ...

        @Override
        public boolean visit(MethodInvocation node) {
            IMethodBinding methodBinding = node.resolveMethodBinding();
            if (methodBinding != null) {
                String calledMethodSignature = getMethodSignature(methodBinding);
                if (currentMethodName != null) {
                    callGraph.computeIfAbsent(currentMethodName, k -> new ArrayList<>()).add(calledMethodSignature);
                }
            }
            return true;
        }

        @Override
        public boolean visit(LambdaExpression node) {
            // Lambda表达式可能包含方法调用
            // 访问Lambda体来查找方法调用
            if (node.getBody() instanceof Expression) {
                node.getBody().accept(this);
            } else if (node.getBody() instanceof Block) {
                ((Block) node.getBody()).accept(this);
            }
            return false; // 不记录Lambda表达式作为调用者
        }
    }

    // ... 其他辅助方法 ...

    private String getMethodSignature(IMethodBinding methodBinding) {
        // 处理泛型和复杂类型
        return methodBinding.getName() + getParameterTypes(methodBinding.getParameterTypes());
    }

    private String getParameterTypes(ITypeBinding[] parameterTypes) {
        StringBuilder params = new StringBuilder("(");
        StringJoiner sj = new StringJoiner(", ");
        for (ITypeBinding typeBinding : parameterTypes) {
            sj.add(getTypeName(typeBinding));
        }
        params.append(sj.toString());
        params.append(")");
        return params.toString();
    }

    private String getTypeName(ITypeBinding typeBinding) {
        // 处理泛型和数组类型
        String typeString = typeBinding.getQualifiedName();
        if (typeBinding.isArray()) {
            typeString += "[]";
        } else if (typeBinding.isParameterizedType()) {
            // 收集泛型参数
            ITypeBinding[] typeArguments = typeBinding.getTypeArguments();
            typeString += "<" + String.join(", ", Arrays.stream(typeArguments)
                    .map(this::getTypeName)
                    .toArray(String[]::new)) + ">";
        }
        return typeString;
    }

    private void printCallGraph() {
        // 打印分析结果
        for (Map.Entry<String, List<String>> entry : callGraph.entrySet()) {
            System.out.println("Method: " + entry.getKey() + " calls: " + entry.getValue());
        }
    }
}
