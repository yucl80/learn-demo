package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Map;

public class SmartCollectionUsageAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        Map<String, Integer> collectionUsage = new HashMap<>();

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof NEW) {
                NEW newInstruction = (NEW) instruction;
                String className = newInstruction.getLoadClassType(constantPool).getClassName();

                if (className.equals("java.util.ArrayList")) {
                    collectionUsage.put("ArrayList", collectionUsage.getOrDefault("ArrayList", 0) + 1);
                } else if (className.equals("java.util.HashSet")) {
                    collectionUsage.put("HashSet", collectionUsage.getOrDefault("HashSet", 0) + 1);
                }
                // 可扩展分析其他集合类
            } else if (instruction instanceof InvokeInstruction) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) instruction;
                String methodName = invokeInstruction.getMethodName(constantPool);

                if (methodName.equals("contains")) {
                    collectionUsage.put("contains", collectionUsage.getOrDefault("contains", 0) + 1);
                } else if (methodName.equals("add")) {
                    collectionUsage.put("add", collectionUsage.getOrDefault("add", 0) + 1);
                }
                // 可扩展分析其他方法
            }
        }

        analyzeUsagePatterns(collectionUsage, method);
    }

    private static void analyzeUsagePatterns(Map<String, Integer> collectionUsage, Method method) {
        int containsCount = collectionUsage.getOrDefault("contains", 0);
        int arrayListCount = collectionUsage.getOrDefault("ArrayList", 0);

        if (containsCount > 10 && arrayListCount > 0) {
            System.out.println("Consider replacing ArrayList with HashSet for frequent 'contains' checks in method "
                    + method.getName());
        }
    }
}
