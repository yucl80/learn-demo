package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

public class MemoryLeakAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        // 检测大量对象创建
        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof NEW) {
                NEW newInstruction = (NEW) instruction;
                String className = newInstruction.getLoadClassType(constantPool).getClassName();

                // 假设某些类（如缓存）可能导致内存泄漏
                if (className.startsWith("com.example.cache")) {
                    System.out.println("Potential memory leak detected with class "
                            + className + " in method " + method.getName() + " at instruction "
                            + handle.getPosition());
                }
            }
        }
    }
}
