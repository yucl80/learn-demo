package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnhancedDataFlowAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        Map<Integer, Set<Integer>> defUseChains = new HashMap<>();
        Map<Integer, Integer> lastDef = new HashMap<>();

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof LocalVariableInstruction) {
                LocalVariableInstruction lvi = (LocalVariableInstruction) instruction;
                int index = lvi.getIndex();

                if (lvi instanceof StoreInstruction) {
                    // 记录变量的定义位置
                    lastDef.put(index, handle.getPosition());
                } else if (lvi instanceof LoadInstruction) {
                    // 使用位置：添加到DU链
                    int defPosition = lastDef.getOrDefault(index, -1);
                    defUseChains.computeIfAbsent(index, k -> new HashSet<>()).add(defPosition);
                }
            }
        }

        // 检测无效赋值和冗余计算
        detectDeadStoresAndRedundantComputations(defUseChains, method);
    }

    private static void detectDeadStoresAndRedundantComputations(Map<Integer, Set<Integer>> defUseChains, Method method) {
        for (Map.Entry<Integer, Set<Integer>> entry : defUseChains.entrySet()) {
            int varIndex = entry.getKey();
            Set<Integer> uses = entry.getValue();

            if (uses.size() <= 1) {
                System.out.println("Potential dead store or redundant computation detected for variable index "
                        + varIndex + " in method " + method.getName());
            }
        }
    }
}
