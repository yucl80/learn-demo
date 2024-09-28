package infer;// 简单的ScopeContext类
import java.util.List;
import java.util.Map;

public class ScopeContext {
    private ScopeContext parentScope; // 父作用域引用
    private Map<String, String> variableDeclarations; // 当前作用域的变量声明
    private Map<String, String> classMemberDeclarations; // 当前类的成员变量
    private Map<MethodSignature, String> methodDeclarations; // 当前类的所有方法签名及其返回类型

    private Map<String, Map<String, String>> classFieldDeclarations; // 存储每个类的字段声明
    private Map<String, String> parentClassHierarchy; // 存储每个类的父类信息

    public ScopeContext(Map<String, Map<String, String>> classFieldDeclarations, Map<String, String> parentClassHierarchy) {
        this.classFieldDeclarations = classFieldDeclarations;
        this.parentClassHierarchy = parentClassHierarchy;
    }

    // 查找给定类中的字段声明
    public String findFieldTypeInClass(String objectType, String fieldName) {
        Map<String, String> fieldDeclarations = classFieldDeclarations.get(objectType);
        if (fieldDeclarations != null) {
            return fieldDeclarations.get(fieldName); // 返回字段的类型
        }
        return null;
    }

    // 获取给定类的父类类型
    public String getParentClassType(String objectType) {
        return parentClassHierarchy.get(objectType); // 返回父类类型
    }

    // 获取当前作用域中的变量类型
    public String getVariableTypeInCurrentScope(String variableName) {
        return variableDeclarations.get(variableName);
    }

    // 查找当前作用域中的方法返回类型
    public String findMethodReturnType(String methodName, List<String> argumentTypes) {
        MethodSignature methodSignature = new MethodSignature(methodName, argumentTypes);
        return methodDeclarations.get(methodSignature); // 返回方法的返回类型
    }

    // 获取父作用域
    public ScopeContext getParentScope() {
        return parentScope;
    }

    // 获取类成员变量的类型
    public String getClassMemberVariableType(String variableName) {
        return classMemberDeclarations.get(variableName);
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


