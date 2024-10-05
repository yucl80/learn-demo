package com.yucl.learn.demo.javaparser;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;


import java.util.List;

public class SqlInjectionChecker2 {

    // 检查是否存在SQL注入漏洞
    public static boolean checkForSqlInjection(MethodCallExpr methodCallExpr, JavaParserFacade javaParserFacade) {
        List<Expression> arguments = methodCallExpr.getArguments();

        for (Expression argument : arguments) {
            if (isSqlQuery(argument)) {
                if (isVulnerable(argument, javaParserFacade)) {
                    System.out.println("Potential SQL injection found in method: " + methodCallExpr);
                    return true;
                }
            }
        }
        return false;
    }

    // 判断是否为SQL查询
    private static boolean isSqlQuery(Expression argument) {
        // 简单判断该参数是否为SQL查询字符串的表达式（如包含关键字 SELECT、INSERT 等）
        if (argument.isStringLiteralExpr()) {
            StringLiteralExpr stringLiteral = argument.asStringLiteralExpr();
            String value = stringLiteral.getValue().toUpperCase();
            return value.contains("SELECT") || value.contains("INSERT") || value.contains("UPDATE") || value.contains("DELETE");
        }
        return false;
    }

    // 判断是否存在SQL注入风险
    private static boolean isVulnerable(Expression argument, JavaParserFacade javaParserFacade) {
        if (argument.isBinaryExpr()) {
            BinaryExpr binaryExpr = argument.asBinaryExpr();

            // 检查拼接的左右两边是否包含动态变量
            if (isDynamicExpression(binaryExpr.getLeft(), javaParserFacade) || isDynamicExpression(binaryExpr.getRight(), javaParserFacade)) {
                return true;
            }
        } else if (argument.isNameExpr()) {
            // 变量名表达式，追踪变量来源
            NameExpr nameExpr = argument.asNameExpr();
            ResolvedType resolvedType = javaParserFacade.getType(nameExpr);
            return isDynamicSource(resolvedType);
        }
        return false;
    }

    // 判断表达式是否是动态变量
    private static boolean isDynamicExpression(Expression expression, JavaParserFacade javaParserFacade) {
        if (expression.isStringLiteralExpr()) {
            // 静态字符串，安全
            return false;
        } else if (expression.isNameExpr()) {
            // 变量名，检查其类型或来源
            NameExpr nameExpr = expression.asNameExpr();
            ResolvedType resolvedType = javaParserFacade.getType(nameExpr);
            return isDynamicSource(resolvedType);
        } else if (expression.isMethodCallExpr()) {
            // 方法调用，追踪返回值的来源
            MethodCallExpr methodCallExpr = expression.asMethodCallExpr();
            return isDynamicMethodReturn(methodCallExpr, javaParserFacade);
        }
        return true; // 其他情况默认为动态
    }

    // 判断变量或表达式是否来自动态源
    private static boolean isDynamicSource(ResolvedType resolvedType) {
        // 如果变量来源于外部输入、用户输入，认为是动态变量
        return resolvedType.describe().contains("HttpServletRequest") || resolvedType.describe().contains("Scanner") ||
                resolvedType.describe().contains("BufferedReader") || resolvedType.describe().contains("UserInput");
    }

    // 判断方法调用返回的类型是否动态
    private static boolean isDynamicMethodReturn(MethodCallExpr methodCallExpr, JavaParserFacade javaParserFacade) {
        // 根据符号解析器获取方法的返回类型
        ResolvedType returnType = javaParserFacade.getType(methodCallExpr);

        // 如果返回类型是外部输入或动态生成，认为存在SQL注入风险
        return isDynamicSource(returnType);
    }
}

