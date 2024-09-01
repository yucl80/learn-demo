package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

public class HotspotAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        int maxNestedLoops = 0;
        int currentDepth = 0;

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof IfInstruction || instruction instanceof GotoInstruction) {
                // 条件跳转或循环
                currentDepth++;
                maxNestedLoops = Math.max(maxNestedLoops, currentDepth);
            } else if (instruction instanceof ReturnInstruction) {
                // 方法返回，重置深度
                currentDepth = 0;
            }
        }

        if (maxNestedLoops > 3) {
            System.out.println("Potential hotspot code detected with nested loops depth " + maxNestedLoops
                    + " in method " + method.getName());
        }
    }
}
