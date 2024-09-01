package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReferenceChainAnalyzer {

    private static final Set<String> referenceChains = new HashSet<>();

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof PUTSTATIC || instruction instanceof PUTFIELD) {
                FieldInstruction fieldInstruction = (FieldInstruction) instruction;
                String className = fieldInstruction.getFieldType(constantPool).getSignature();
                String fieldName = fieldInstruction.getFieldName(constantPool);

                referenceChains.add(className + "." + fieldName);
            }
        }

        detectPotentialReferenceLeaks(method);
    }

    private static void detectPotentialReferenceLeaks(Method method) {
        if (!referenceChains.isEmpty()) {
            System.out.println("Potential reference chains detected in method " + method.getName() + ": " + referenceChains);
        }
    }
}
