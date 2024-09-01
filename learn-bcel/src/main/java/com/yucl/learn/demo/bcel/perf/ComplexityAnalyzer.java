package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashSet;
import java.util.Set;

public class ComplexityAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        Set<InstructionHandle> visited = new HashSet<>();
        int complexity = calculateCyclomaticComplexity(instructionList, visited);

        if (complexity > 10) {
            System.out.println("High cyclomatic complexity detected (" + complexity + ") in method "
                    + method.getName());
        }
    }

    private static int calculateCyclomaticComplexity(InstructionList instructionList, Set<InstructionHandle> visited) {
        // 简化计算复杂度的方法（真实应用中可能更复杂）
        int complexity = 1; // 基础复杂度

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof IfInstruction || instruction instanceof GotoInstruction) {
                complexity++;
            }
        }

        return complexity;
    }
}
