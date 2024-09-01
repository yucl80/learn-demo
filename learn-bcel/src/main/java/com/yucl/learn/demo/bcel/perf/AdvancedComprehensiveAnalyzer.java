package com.yucl.learn.demo.bcel.perf;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

public class AdvancedComprehensiveAnalyzer {

    public static void analyzeClass(JavaClass javaClass) {
        ConstantPoolGen constantPool = new ConstantPoolGen(javaClass.getConstantPool());
        for (Method method : javaClass.getMethods()) {
            EnhancedDataFlowAnalyzer.analyzeMethod(method, constantPool);
            DeadlockAnalyzer.analyzeMethod(method, constantPool);
            SmartCollectionUsageAnalyzer.analyzeMethod(method, constantPool);
            HotspotAnalyzer.analyzeMethod(method, constantPool);
            // 其他分析器...
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: AdvancedComprehensiveAnalyzer <class file>");
            return;
        }

        String className = args[0];
        JavaClass javaClass = new ClassParser(className).parse();
        analyzeClass(javaClass);
    }
}
