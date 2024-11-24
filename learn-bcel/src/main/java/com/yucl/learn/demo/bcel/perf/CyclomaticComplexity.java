package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

public class CyclomaticComplexity {
    public static int computeCyclomaticComplexity(String className, String methodName) throws Exception {
        ClassParser cp = new ClassParser(className);
        JavaClass jc = cp.parse();

        Method m = null;
        for (Method method : jc.getMethods()) {
            if (method.getName().equals(methodName)) {
                m = method;
                break;
            }
        }

        if (m == null) {
            System.out.println("No method found: " + methodName);
            return 0;
        }

        ConstantPoolGen cpg = new ConstantPoolGen(jc.getConstantPool());
        MethodGen methodGen = new MethodGen(m, jc.getClassName(), cpg);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return 1; // Base complexity for empty method
        }

        int cyclomaticComplexity = 1; // Base complexity

        for (InstructionHandle ih = instructionList.getStart(); ih != null; ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();

            if (inst instanceof org.apache.bcel.generic.GOTO ||
                    inst instanceof org.apache.bcel.generic.GOTO_W ||
                    inst instanceof org.apache.bcel.generic.IF_ACMPEQ ||
                    inst instanceof org.apache.bcel.generic.IF_ACMPNE ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPEQ ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPNE ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPLT ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPGE ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPGT ||
                    inst instanceof org.apache.bcel.generic.IF_ICMPLE ||
                    inst instanceof org.apache.bcel.generic.IFEQ ||
                    inst instanceof org.apache.bcel.generic.IFNE ||
                    inst instanceof org.apache.bcel.generic.IFLT ||
                    inst instanceof org.apache.bcel.generic.IFGE ||
                    inst instanceof org.apache.bcel.generic.IFGT ||
                    inst instanceof org.apache.bcel.generic.IFLE ||
                    inst instanceof org.apache.bcel.generic.JSR ||
                    inst instanceof org.apache.bcel.generic.JSR_W ||
                    inst instanceof org.apache.bcel.generic.LOOKUPSWITCH ||
                    inst instanceof org.apache.bcel.generic.TABLESWITCH) {
                cyclomaticComplexity++;

            }
        }

        return cyclomaticComplexity;

    }
}