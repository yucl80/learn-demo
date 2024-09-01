package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DeadlockAnalyzer {

    public static void analyzeMethod(Method method, ConstantPoolGen constantPool) {
        MethodGen methodGen = new MethodGen(method, method.getName(), constantPool);
        InstructionList instructionList = methodGen.getInstructionList();

        if (instructionList == null) {
            return;
        }

        Set<String> locksHeld = new HashSet<>();
        Map<String, Set<String>> lockGraph = new HashMap<>();

        for (InstructionHandle handle : instructionList) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof MONITORENTER) {
                MONITORENTER monitorEnter = (MONITORENTER) instruction;
                String lockName = getLockName(handle, constantPool);

                for (String heldLock : locksHeld) {
                    lockGraph.computeIfAbsent(heldLock, k -> new HashSet<>()).add(lockName);
                }

                locksHeld.add(lockName);
            }

            if (instruction instanceof MONITOREXIT) {
                MONITOREXIT monitorExit = (MONITOREXIT) instruction;
                String lockName = getLockName(handle, constantPool);
                locksHeld.remove(lockName);
            }
        }

        detectPotentialDeadlocks(lockGraph, method);
    }

    private static String getLockName(InstructionHandle handle, ConstantPoolGen constantPool) {
        // 获取锁的名称（可以基于具体的类名或对象引用）
        return "Lock@" + handle.getPosition();
    }

    private static void detectPotentialDeadlocks(Map<String, Set<String>> lockGraph, Method method) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String lock : lockGraph.keySet()) {
            if (detectCycle(lock, lockGraph, visited, recursionStack)) {
                System.out.println("Potential deadlock detected involving lock " + lock + " in method " + method.getName());
            }
        }
    }

    private static boolean detectCycle(String lock, Map<String, Set<String>> lockGraph, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(lock)) {
            return true;
        }

        if (visited.contains(lock)) {
            return false;
        }

        visited.add(lock);
        recursionStack.add(lock);

        Set<String> nextLocks = lockGraph.get(lock);
        if (nextLocks != null) {
            for (String nextLock : nextLocks) {
                if (detectCycle(nextLock, lockGraph, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(lock);
        return false;
    }
}
