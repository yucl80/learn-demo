package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SQLInjectionAnalyzer {

    private static final Map<String, Set<String>> methodMap = new HashMap<>();

    static {
        methodMap.put("java.sql.Statement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("java.sql.PreparedStatement", Set.of("executeQuery", "executeUpdate", "execute"));
        methodMap.put("org.springframework.jdbc.core.JdbcTemplate", Set.of("query", "update", "execute"));
        methodMap.put("org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate", Set.of("query", "update", "execute"));
        methodMap.put("org.springframework.data.jpa.repository.JpaRepository", Set.of("findAll", "findOne"));
        // Add more entries as needed for other classes and methods
    }

    public static void main(String[] args) throws IOException {
        JavaClass javaClass = new ClassParser("YourClass.class").parse();
        ConstantPoolGen constantPoolGen = new ConstantPoolGen(javaClass.getConstantPool());

        for (Method method : javaClass.getMethods()) {
            MethodGen methodGen = new MethodGen(method, javaClass.getClassName(), constantPoolGen);
            InstructionList instructionList = methodGen.getInstructionList();

            if (instructionList != null) {
                analyzeInstructions(instructionList, methodGen, constantPoolGen);
            }
        }
    }

    private static void analyzeInstructions(InstructionList instructionList, MethodGen methodGen, ConstantPoolGen constantPoolGen) {
        InstructionHandle[] handles = instructionList.getInstructionHandles();

        for (InstructionHandle handle : handles) {
            Instruction instruction = handle.getInstruction();

            if (instruction instanceof InvokeInstruction) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) instruction;
                String className = invokeInstruction.getClassName(constantPoolGen);
                String methodName = invokeInstruction.getMethodName(constantPoolGen);

                if (methodMap.containsKey(className) && methodMap.get(className).contains(methodName)) {
                    analyzeSQLStatementConstruction(handle, methodGen, constantPoolGen);
                }
            }
        }
    }

    private static void analyzeSQLStatementConstruction(InstructionHandle executionHandle, MethodGen methodGen, ConstantPoolGen constantPoolGen) {
        InstructionHandle currentHandle = executionHandle.getPrev();

        while (currentHandle != null) {
            Instruction instruction = currentHandle.getInstruction();

            if (instruction instanceof InvokeInstruction) {
                InvokeInstruction invokeInstruction = (InvokeInstruction) instruction;
                String invokedClassName = invokeInstruction.getClassName(constantPoolGen);
                String invokedMethodName = invokeInstruction.getMethodName(constantPoolGen);

                if (invokedClassName.equals("java.lang.StringBuilder") || invokedClassName.equals("java.lang.StringBuffer")) {
                    if (invokedMethodName.equals("append")) {
                        System.out.println("Potential SQL injection risk detected in method: " + methodGen.getName());
                        return;
                    }
                }

                if (invokedMethodName.equals("concat") || invokedClassName.equals("java.lang.String")) {
                    System.out.println("Potential SQL injection risk detected in method: " + methodGen.getName());
                    return;
                }
            }

            currentHandle = currentHandle.getPrev();
        }
    }
}

