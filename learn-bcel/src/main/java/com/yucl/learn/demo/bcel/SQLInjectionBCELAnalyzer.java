package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SQLInjectionBCELAnalyzer {
    private static final Set<String> SQL_METHODS = Set.of(
            "executeQuery", "executeUpdate", "execute", "executeLargeUpdate",
            "query", "update", "executeFunction", "executeProcedure"
    );

    private static final Set<String> STRING_BUILDER_METHODS = Set.of("append");

    private static final Set<String> DYNAMIC_SQL_METHODS = Set.of(
            "concat", "format", "join", "replace", "substring", "toLowerCase", "toUpperCase", "insert"
    );

    private static final Set<String> STRING_TYPES = Set.of("java/lang/String");

    public static void main(String[] args) throws IOException {
        String classFilePath = "path/to/your/compiled/ClassFile.class";
        analyzeClassFile(classFilePath);
    }

    public static void analyzeClassFile(String classFilePath) throws IOException {

            String fis = new String(Files.readAllBytes(Paths.get(classFilePath)));

            ClassParser parser = new ClassParser(fis);
            JavaClass javaClass = parser.parse();

            for (Method method : javaClass.getMethods()) {
                analyzeMethod(method);
            }

    }

    private static void analyzeMethod(Method method) {
        MethodGen methodGen = new MethodGen(method, "className", new ConstantPoolGen());
        InstructionList il = methodGen.getInstructionList();

        if (il != null) {
            InstructionHandle[] instructions = il.getInstructionHandles();
            for (InstructionHandle handle : instructions) {
                Instruction instruction = handle.getInstruction();
                if (instruction instanceof INVOKEVIRTUAL || instruction instanceof INVOKEINTERFACE || instruction instanceof INVOKESPECIAL || instruction instanceof INVOKESTATIC) {
                    MethodInvocationAnalyzer analyzer = new MethodInvocationAnalyzer((InvokeInstruction) instruction);
                    analyzer.analyze();
                }
            }
        }
    }

    private static class MethodInvocationAnalyzer {
        private final InvokeInstruction invokeInstruction;

        MethodInvocationAnalyzer(InvokeInstruction invokeInstruction) {
            this.invokeInstruction = invokeInstruction;
        }

        void analyze() {
            String className = invokeInstruction.getClassName(null);
            String methodName = invokeInstruction.getMethodName(null);

            if (SQL_METHODS.contains(methodName)) {
                System.out.println("Potential SQL Injection risk in method: " + methodName);
                // You can add more detailed analysis here if needed
            }

            if (STRING_BUILDER_METHODS.contains(methodName) && className.equals("java/lang/StringBuilder")) {
                System.out.println("Potential SQL Injection risk in StringBuilder method: " + methodName);
                // You can add more detailed analysis here if needed
            }

            if (DYNAMIC_SQL_METHODS.contains(methodName) && STRING_TYPES.contains(className)) {
                System.out.println("Potential SQL Injection risk in dynamic SQL method: " + methodName);
                // You can add more detailed analysis here if needed
            }
        }
    }
}

