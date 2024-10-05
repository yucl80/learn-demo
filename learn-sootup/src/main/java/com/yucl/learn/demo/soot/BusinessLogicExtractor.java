package com.yucl.learn.demo.soot;

import soot.*;
import soot.dava.internal.AST.ASTTryNode;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.graph.*;


import java.util.*;



public class BusinessLogicExtractor {

    private static List<String> identifiedBusinessLogicMethods = new ArrayList<>();

    public static void main(String[] args) {
        // Step 1: Set up Soot
        String classPath = "path/to/your/classes:path/to/your/jarfile.jar"; // Include both class files and JAR files
        String mainClass = "YourMainClass"; // Specify the main class to analyze

        // Step 2: Configure Soot
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(Collections.singletonList(classPath));
        Options.v().set_output_format(Options.output_format_jimple); // Set output format to Jimple
        Scene.v().loadNecessaryClasses();

        // Step 3: Load the main class
        SootClass sootClass = Scene.v().loadClassAndSupport(mainClass);
        sootClass.setApplicationClass();

        // Step 4: Generate call graphs and analyze methods
        for (SootMethod method : sootClass.getMethods()) {
            if (method.isConcrete()) {
                System.out.println("Analyzing method: " + method.getName());
                Body body = method.getActiveBody();

                // Step 5: Print Jimple representation
                System.out.println("Jimple Representation:\n" + body.toString());

                // Step 6: Perform data-flow analysis and identify business logic
                performDataFlowAnalysis(body);
            }
        }

        // Step 7: Print summary of identified business logic methods
        printSummary();
    }

    private static void performDataFlowAnalysis(Body body) {
        System.out.println("Performing data-flow analysis on method: " + body.getMethod().getName());

        // Create a control flow graph for the method
        UnitGraph graph = new BriefUnitGraph(body);

        // Iterate over the units in the graph
        for (Unit unit : graph) {
            System.out.println("Analyzing unit: " + unit.toString());

            // Identify specific patterns that may indicate business logic
            identifyBusinessLogic(unit);

            // Print used values for further analysis
            for (ValueBox vb : unit.getUseBoxes()) {
                Value value = vb.getValue();
                System.out.println("Used value: " + value.toString());
            }

            // Check for exception handling blocks
            if (unit instanceof Stmt) {
                checkForExceptionHandling(unit);
            }
        }
    }

    private static void identifyBusinessLogic(Unit unit) {
        if (unit instanceof InvokeStmt) {
            InvokeExpr invokeExpr = ((InvokeStmt) unit).getInvokeExpr();
            SootMethod invokedMethod = invokeExpr.getMethod();

            if (isBusinessLogicMethod(invokedMethod)) {
                System.out.println("Identified potential business logic in method call: " + invokedMethod.getName());
                identifiedBusinessLogicMethods.add(invokedMethod.getName());
            }
        }
    }

    private static boolean isBusinessLogicMethod(SootMethod method) {
        List<String> businessLogicPatterns = Arrays.asList("process", "calculate", "validate", "save", "update", "execute", "handle");

        for (String pattern : businessLogicPatterns) {
            if (method.getName().toLowerCase().contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    private static void checkForExceptionHandling(Unit unit) {
        if (unit instanceof ASTTryNode) {
            ASTTryNode tryCatchStmt = (ASTTryNode) unit;
            System.out.println("Found exception handling in try-catch block.");
            System.out.println("Try block: " + tryCatchStmt.get_TryBody());
            System.out.println("Catch blocks: " + tryCatchStmt.get_CatchList());
        }
    }

    private static void printSummary() {
        System.out.println("\n--- Summary of Identified Business Logic Methods ---");

        if (identifiedBusinessLogicMethods.isEmpty()) {
            System.out.println("No business logic methods identified.");
        } else {
            for (String methodName : identifiedBusinessLogicMethods) {
                System.out.println("Identified Business Logic Method: " + methodName);
            }
        }
    }
}