package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImprovedMemoryLeakAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        Set<String> cacheClasses = new HashSet<>();
        Map<String, Integer> objectCreationCount = new HashMap<>();
        Set<String> longLivedObjects = new HashSet<>();

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof NEW) {
                NEW newInstruction = (NEW) instruction;
                String className = newInstruction.getLoadClassType(constantPool).getClassName();

                if (isCacheClass(className)) {
                    cacheClasses.add(className);
                }

                objectCreationCount.put(className, objectCreationCount.getOrDefault(className, 0) + 1);
            }

            if (instruction instanceof PUTSTATIC) {
                PUTSTATIC putStatic = (PUTSTATIC) instruction;
                String fieldName = putStatic.getFieldName(constantPool);
                String className = putStatic.getFieldType(constantPool).getSignature();

                if (cacheClasses.contains(className)) {
                    longLivedObjects.add(fieldName);
                    System.out.println("Potential long-lived object detected in class " + className
                            + " for field " + fieldName + " in method " + method.getName());
                }
            }
        }

        analyzeObjectCreation(objectCreationCount, method);
    }

    private static boolean isCacheClass(String className) {
        // 这里可以根据实际需求修改为检查特定的缓存类
        return className.startsWith("com.example.cache");
    }

    private static void analyzeObjectCreation(Map<String, Integer> objectCreationCount, Method method) {
        for (Map.Entry<String, Integer> entry : objectCreationCount.entrySet()) {
            String className = entry.getKey();
            int count = entry.getValue();

            if (count > 100) { // 示例阈值，实际可以更具体
                System.out.println("High object creation count detected for class "
                        + className + " in method " + method.getName() + ": " + count);
            }
        }
    }
}

