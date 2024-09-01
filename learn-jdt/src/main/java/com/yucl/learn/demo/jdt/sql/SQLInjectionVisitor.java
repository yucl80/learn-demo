package com.yucl.learn.demo.jdt.sql;

import org.eclipse.jdt.core.dom.*;

import java.util.*;

public class SQLInjectionVisitor extends ASTVisitor {
    private Set<String> methodParameters = new HashSet<>();
    private Map<String, Set<String>> methodMap = new HashMap<>();
    private Set<String> dynamicSQLMethods = new HashSet<>();
    private Set<String> stringBuilders = new HashSet<>(Arrays.asList("java.lang.StringBuilder", "java.lang.StringBuffer"));

    public SQLInjectionVisitor() {
        // 初始化 methodMap，存储数据库访问类型与方法的对应关系
        methodMap.put("java.sql.Connection", Set.of("prepareStatement", "prepareCall"));
        methodMap.put("java.sql.Statement", Set.of("executeQuery", "executeUpdate", "execute", "executeLargeUpdate"));
        methodMap.put("java.sql.PreparedStatement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("java.sql.CallableStatement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("org.springframework.jdbc.core.JdbcTemplate", Set.of("execute", "update", "query", "batchUpdate", "queryForMap", "queryForList", "queryForObject", "queryForRowSet", "queryForStream"));
        methodMap.put("org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate", Set.of("execute", "update", "query", "batchUpdate", "queryForMap", "queryForList", "queryForObject", "queryForRowSet", "queryForStream"));
        methodMap.put("org.springframework.jdbc.core.simple.SimpleJdbcCall", Set.of("execute", "executeFunction", "executeProcedure"));
        methodMap.put("org.hibernate.Session", Set.of("createQuery", "createSQLQuery", "update"));
        methodMap.put("org.springframework.data.jpa.repository.JpaRepository", Set.of("findAll", "findById"));
        // 添加其他数据库访问类型及其方法

        // 初始化动态 SQL 生成方法集合
        dynamicSQLMethods.addAll(Arrays.asList("concat", "format", "join", "replace", "substring", "toLowerCase", "toUpperCase", "append", "insert", "addAll"));
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        // 收集方法的参数名称
        for (Object param : node.parameters()) {
            SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration) param;
            methodParameters.add(variableDeclaration.getName().getIdentifier());
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        String methodName = node.getName().toString();
        Expression expr = node.getExpression();
        if (expr != null && expr.resolveTypeBinding() != null) {
            String typeName = expr.resolveTypeBinding().getQualifiedName();

            // 检测是否是数据库访问类型的方法调用
            if (methodMap.containsKey(typeName) && methodMap.get(typeName).contains(methodName)) {
                checkForParameterInSQL(node);
            }

            // 检测字符串拼接（StringBuilder, StringBuffer）
            if (stringBuilders.contains(typeName) && "append".equals(methodName)) {
                checkForParameterInAppend(node);
            }

            // 检测动态 SQL 生成方法
            if (dynamicSQLMethods.contains(methodName) && isStringType(typeName)) {
                checkForParameterInDynamicSQL(node);
            }

            // 检测 Map 和 List 类型的 SQL 拼接
            if (typeName.equals("java.util.Map") || typeName.equals("java.util.List")) {
                checkForDynamicSQLInCollections(node);
            }
        }
        return super.visit(node);
    }

    private boolean isStringType(String typeName) {
        return "java.lang.String".equals(typeName);
    }

    private void checkForParameterInSQL(MethodInvocation node) {
        for (Object arg : node.arguments()) {
            if (arg instanceof Expression) {
                Expression expr = (Expression) arg;
                if (containsParameter(expr)) {
                    reportPotentialRisk(expr);
                }
            }
        }
    }

    private void checkForParameterInDynamicSQL(MethodInvocation node) {
        for (Object arg : node.arguments()) {
            if (arg instanceof Expression) {
                Expression expr = (Expression) arg;
                if (containsParameter(expr)) {
                    reportPotentialRisk(expr);
                }
            }
        }
    }

    private void checkForParameterInAppend(MethodInvocation node) {
        for (Object arg : node.arguments()) {
            if (arg instanceof Expression) {
                Expression expr = (Expression) arg;
                if (containsParameter(expr)) {
                    reportPotentialRisk(expr);
                }
            }
        }
    }

    private void checkForDynamicSQLInCollections(MethodInvocation node) {
        for (Object arg : node.arguments()) {
            if (arg instanceof Expression) {
                Expression expr = (Expression) arg;
                if (expr instanceof MethodInvocation) {
                    MethodInvocation collectionInvocation = (MethodInvocation) expr;
                    String methodName = collectionInvocation.getName().toString();
                    if ("get".equals(methodName) || "toString".equals(methodName) || "add".equals(methodName) || "put".equals(methodName)) {
                        checkForParameterInDynamicSQL(collectionInvocation);
                    }
                }
            }
        }
    }

    private boolean containsParameter(Expression expression) {
        if (expression instanceof SimpleName) {
            return methodParameters.contains(expression.toString());
        } else if (expression instanceof InfixExpression) {
            InfixExpression infixExpr = (InfixExpression) expression;
            return containsParameter(infixExpr.getLeftOperand()) || containsParameter(infixExpr.getRightOperand());
        } else if (expression instanceof MethodInvocation) {
            MethodInvocation methodInvocation = (MethodInvocation) expression;
            for (Object arg : methodInvocation.arguments()) {
                if (arg instanceof Expression && containsParameter((Expression) arg)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void reportPotentialRisk(Expression expr) {
        String exprString = expr.toString();
        if (isSQLInjectionPattern(exprString)) {
            System.out.println("Potential SQL Injection Risk detected in expression: " + exprString);
            if (expr.getParent() instanceof MethodInvocation) {
                MethodInvocation parentInvocation = (MethodInvocation) expr.getParent();
                System.out.println("Called in method: " + parentInvocation.getName());
            }
            if (expr.getParent() instanceof Statement) {
                Statement parentStatement = (Statement) expr.getParent();
                System.out.println("Containing statement: " + parentStatement);
            }
            System.out.println("Consider using parameterized queries or prepared statements to prevent SQL injection.");
        }
    }

    private boolean isSQLInjectionPattern(String expression) {
        // 使用正则表达式来检测更复杂的 SQL 注入模式
        String[] sqlKeywords = {"SELECT", "INSERT", "UPDATE", "DELETE", "WHERE", "OR", "AND", "UNION", "LIKE"};
        for (String keyword : sqlKeywords) {
            if (expression.toUpperCase().matches(".*\\b" + keyword + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}


