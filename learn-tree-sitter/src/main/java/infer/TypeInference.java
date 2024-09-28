package infer;

import java.util.ArrayList;
import java.util.List;

public class TypeInference {


    // 推断参数类型
    public String inferArgumentType(ASTNode argumentNode, ScopeContext scopeContext) {
        // 1. 字面量类型推断
        if ("integer_literal".equals(argumentNode.getType())) {
            return "int";
        } else if ("floating_point_literal".equals(argumentNode.getType())) {
            return "float";
        } else if ("string_literal".equals(argumentNode.getType())) {
            return "String";
        } else if ("boolean_literal".equals(argumentNode.getType())) {
            return "boolean";
        } else if ("null_literal".equals(argumentNode.getType())) {
            return "null";
        }

        // 2. 变量类型推断 (局部变量、成员变量)
        if ("identifier".equals(argumentNode.getType())) {
            String variableName = argumentNode.getText();
            String variableType = findVariableType(variableName, scopeContext);
            return (variableType != null) ? variableType : "Unknown";
        }

        // 3. 方法调用类型推断
        if ("method_invocation".equals(argumentNode.getType())) {
            ASTNode methodNameNode = argumentNode.getChild("name");
            if (methodNameNode != null) {
                String methodName = methodNameNode.getText();
                String returnType = findMethodReturnType(methodName, argumentNode,scopeContext);
                return (returnType != null) ? returnType : "Unknown";
            }
        }

        // 4. 对象成员访问，如 `object.field`
        if ("field_access".equals(argumentNode.getType())) {
            ASTNode fieldNode = argumentNode.getChild("field");
            ASTNode objectNode = argumentNode.getChild("object");
            if (objectNode != null && fieldNode != null) {
                String objectType = inferArgumentType(objectNode, scopeContext);
                return findFieldType(objectType, fieldNode.getText(),scopeContext);
            }
        }

        // 5. 类型转换，如 `(int) value`
        if ("type_cast".equals(argumentNode.getType())) {
            ASTNode castTypeNode = argumentNode.getChild("type");
            return (castTypeNode != null) ? castTypeNode.getText() : "Unknown";
        }

        // 6. 数组类型推断
        if ("array_creation_expression".equals(argumentNode.getType())) {
            ASTNode elementTypeNode = argumentNode.getChild("element_type");
            return (elementTypeNode != null) ? elementTypeNode.getText() + "[]" : "Unknown[]";
        }

        // 7. 泛型类型推断
        if ("generic_type".equals(argumentNode.getType())) {
            ASTNode baseTypeNode = argumentNode.getChild("base_type");
            ASTNode genericArgsNode = argumentNode.getChild("generic_arguments");
            if (baseTypeNode != null && genericArgsNode != null) {
                String baseType = baseTypeNode.getText();
                List<ASTNode> genericArgs = genericArgsNode.getChildren();
                StringBuilder genericTypeBuilder = new StringBuilder(baseType + "<");
                for (int i = 0; i < genericArgs.size(); i++) {
                    genericTypeBuilder.append(inferArgumentType(genericArgs.get(i), scopeContext));
                    if (i < genericArgs.size() - 1) {
                        genericTypeBuilder.append(", ");
                    }
                }
                genericTypeBuilder.append(">");
                return genericTypeBuilder.toString();
            }
        }

        // 8. 算术运算类型推断
        if ("binary_expression".equals(argumentNode.getType()) || "unary_expression".equals(argumentNode.getType())) {
            ASTNode leftNode = argumentNode.getChild("left");
            ASTNode rightNode = argumentNode.getChild("right");
            String leftType = inferArgumentType(leftNode, scopeContext);
            String rightType = inferArgumentType(rightNode, scopeContext);

            // 假设优先级：double > float > int
            if ("double".equals(leftType) || "double".equals(rightType)) {
                return "double";
            } else if ("float".equals(leftType) || "float".equals(rightType)) {
                return "float";
            } else if ("int".equals(leftType) || "int".equals(rightType)) {
                return "int";
            }
        }

        // 9. 默认情况：未知类型
        return "Unknown";
    }

    // 查找变量的类型 (例如在当前作用域中查找变量声明)
    private String findVariableType(String variableName, ScopeContext scopeContext) {
        // 1. 先在当前作用域中查找
        String variableType = scopeContext.getVariableTypeInCurrentScope(variableName);
        if (variableType != null) {
            return variableType; // 如果在当前作用域找到变量，返回类型
        }

        // 2. 如果当前作用域找不到，尝试向上层作用域递归查找
        ScopeContext parentScope = scopeContext.getParentScope();
        if (parentScope != null) {
            return findVariableType(variableName, parentScope); // 向父作用域递归查找
        }

        // 3. 如果作用域链中都找不到，再查找类成员或全局范围的变量
        String classMemberType = scopeContext.getClassMemberVariableType(variableName);
        if (classMemberType != null) {
            return classMemberType; // 返回类成员变量类型
        }

        // 4. 如果依然找不到，则返回未知类型
        return "Unknown";
    }


    // 查找方法的返回类型
    private String findMethodReturnType(String methodName, ASTNode methodNode, ScopeContext scopeContext) {
        // 获取调用方法的参数类型
        List<String> argumentTypes = getMethodArgumentTypes(methodNode, scopeContext);

        // 1. 在当前类或作用域中查找方法
        String returnType = scopeContext.findMethodReturnType(methodName, argumentTypes);
        if (returnType != null) {
            return returnType;
        }

        // 2. 如果当前类找不到该方法，递归查找父类或接口中的方法
        ScopeContext parentScope = scopeContext.getParentScope();
        if (parentScope != null) {
            return findMethodReturnType(methodName, methodNode, parentScope); // 向父作用域或类递归查找
        }

        // 3. 如果没有找到，返回未知类型
        return "Unknown";
    }

    // 获取方法参数的类型列表
    private List<String> getMethodArgumentTypes(ASTNode methodNode, ScopeContext scopeContext) {
        List<ASTNode> argumentNodes = methodNode.getChildren("arguments");
        List<String> argumentTypes = new ArrayList<>();

        for (ASTNode argumentNode : argumentNodes) {
            String argumentType = inferArgumentType(argumentNode, scopeContext);
            argumentTypes.add(argumentType);
        }

        return argumentTypes;
    }


    // 查找对象字段的类型
    private String findFieldType(String objectType, String fieldName, ScopeContext scopeContext) {
        // 1. 查找当前类中的字段
        String fieldType = scopeContext.findFieldTypeInClass(objectType, fieldName);
        if (fieldType != null) {
            return fieldType; // 如果在当前类中找到字段，返回字段类型
        }

        // 2. 如果在当前类找不到该字段，递归查找父类中的字段
        String parentClassType = scopeContext.getParentClassType(objectType);
        if (parentClassType != null) {
            return findFieldType(parentClassType, fieldName, scopeContext); // 向父类递归查找
        }

        // 3. 如果没有找到，返回未知类型
        return "Unknown";
    }


}
