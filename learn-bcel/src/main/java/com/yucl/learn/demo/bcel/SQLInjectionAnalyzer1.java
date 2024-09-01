package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.ClassPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SQLInjectionAnalyzer1 {

    private static final Set<String> SQL_METHODS = new HashSet<>();
    private static final Set<String> STRING_METHODS = new HashSet<>();
    private static final String STRING_BUILDER_CLASS = "java.lang.StringBuilder";
    private static final String STRING_BUFFER_CLASS = "java.lang.StringBuffer";

    static {
        // 常见的SQL执行方法
        SQL_METHODS.add("executeQuery");
        SQL_METHODS.add("executeUpdate");
        SQL_METHODS.add("execute");

        // 常见的字符串操作方法
        STRING_METHODS.add("concat");
        STRING_METHODS.add("append");
    }

    public static void main(String[] args) throws IOException {
        String className = "com.example.YourClass"; // 替换为你要分析的类名
        analyzeClassForSQLInjection(className);
    }

    public static void analyzeClassForSQLInjection(String className) throws IOException {
        ClassPath classPath = new ClassPath(System.getProperty("java.class.path"));
        JavaClass javaClass = new ClassParser(classPath.getPath(className), className + ".class").parse();
        ConstantPoolGen constantPoolGen=new ConstantPoolGen(javaClass.getConstantPool());
        for (Method method : javaClass.getMethods()) {
            analyzeMethodForSQLInjection(javaClass, method, constantPoolGen);
        }
    }

    private static void analyzeMethodForSQLInjection(JavaClass javaClass, Method method,ConstantPoolGen cp) {
        MethodGen mg = new MethodGen(method, javaClass.getClassName(), cp);
        InstructionList il = mg.getInstructionList();

        if (il == null) return;

        boolean possibleInjectionPoint = false;
        boolean staticStringOnly = true;

        // 跟踪StringBuilder或StringBuffer对象的状态
        Map<Integer, Boolean> stringBuilderStates = new HashMap<>();
        Set<Integer> localVariables = new HashSet<>();

        for (InstructionHandle ih : il.getInstructionHandles()) {
            Instruction inst = ih.getInstruction();

            if (inst instanceof LDC) {
                // 处理字面量加载 (LDC) 指令
                LDC ldc = (LDC) inst;
                if (!(ldc.getValue(cp) instanceof String)) {
                    staticStringOnly = false;
                }
            } else if (inst instanceof LoadInstruction) {
                // 处理局部变量加载 (ILOAD, ALOAD) 指令
                int index = ((LoadInstruction) inst).getIndex();
                if (localVariables.contains(index)) {
                    staticStringOnly = false;
                }
            } else if (inst instanceof StoreInstruction) {
                // 处理局部变量存储 (ISTORE, ASTORE) 指令
                int index = ((StoreInstruction) inst).getIndex();
                localVariables.add(index);
            } else if (inst instanceof InvokeInstruction) {
                InvokeInstruction invoke = (InvokeInstruction) inst;
                String methodName = invoke.getMethodName(cp);
                String className = invoke.getClassName(cp);

                // 检查是否是 SQL 执行方法
                if (SQL_METHODS.contains(methodName) && className.startsWith("java.sql.")) {
                    possibleInjectionPoint = true;
                }

                // 检查字符串拼接操作
                if ((STRING_METHODS.contains(methodName) && className.equals("java.lang.String")) ||
                        (STRING_METHODS.contains(methodName) && (className.equals(STRING_BUILDER_CLASS) || className.equals(STRING_BUFFER_CLASS)))) {
                    ReferenceType refType = ((InvokeInstruction) inst).getReferenceType(cp);
                    System.out.println("String operation detected in method: " + mg.getName() + " at instruction: " + ih);
                    if (!isStaticString(invoke, cp, localVariables, stringBuilderStates, refType)) {
                        staticStringOnly = false;
                    }
                }
            } else if (inst instanceof NEW) {
                // 检查 StringBuilder 或 StringBuffer 对象的创建
                NEW newInst = (NEW) inst;
                String typeName = newInst.getLoadClassType(cp).getClassName();
                if (typeName.equals(STRING_BUILDER_CLASS) || typeName.equals(STRING_BUFFER_CLASS)) {
                    int index = ((NEW) inst).getIndex();
                    stringBuilderStates.put(index, true);  // 默认假设新创建的对象是空的，即静态的
                }
            }

            if (possibleInjectionPoint && !staticStringOnly) {
                System.out.println("Potential SQL Injection detected in method: " + mg.getName() + " at instruction: " + ih);
                possibleInjectionPoint = false; // 重置以检测下一个可能的SQL调用
                staticStringOnly = true; // 重置以检测下一个可能的SQL调用
                localVariables.clear(); // 重置本地变量集合
                stringBuilderStates.clear(); // 重置 StringBuilder 状态
            }
        }
    }

    private static boolean isStaticString(InvokeInstruction invoke, ConstantPoolGen cp, Set<Integer> localVariables, Map<Integer, Boolean> stringBuilderStates, ReferenceType refType) {
        // 检查是否是静态字符串（即字面量字符串）
        if (invoke instanceof INVOKEVIRTUAL || invoke instanceof INVOKESPECIAL) {
            Type[] argTypes = invoke.getArgumentTypes(cp);
            for (Type argType : argTypes) {
                if (!argType.equals(Type.STRING)) {
                    return false; // 如果类型不是字符串
                }
            }
            // 检查 StringBuilder 或 StringBuffer 的状态
//            if (stringBuilderStates.containsKey(refType.getClassName()) && !stringBuilderStates.get(objIndex)) {
//                return false; // 如果 StringBuilder 不是静态的，返回 false
//            }
        }
        return true;
    }
}
