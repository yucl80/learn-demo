package com.yucl.learn.demo.soot;

import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.graph.*;

import java.util.*;

public class MethodResolutionExample {

    public static void main(String[] args) {
        // Set up your Soot project and view here
        String classPath = "path/to/your/classes:path/to/your/jarfile.jar";
        String mainClass = "YourMainClass";

        // Initialize Soot
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_process_dir(Collections.singletonList(classPath));
        Scene.v().loadNecessaryClasses();

        // Load the main class
        SootClass sootClass = Scene.v().loadClassAndSupport(mainClass);
        sootClass.setApplicationClass();

        // Create the type hierarchy view
        Hierarchy typeHierarchy = Scene.v().getActiveHierarchy();

        // Analyze methods in the loaded class
        for (SootMethod method : sootClass.getMethods()) {
            if (method.isConcrete()) {
                Body body = method.getActiveBody();
                UnitGraph graph = new BriefUnitGraph(body);

                // Iterate through units in the control flow graph
                for (Unit unit : graph) {
                    if (unit instanceof InvokeStmt) {
                        InvokeExpr invokeExpr = ((InvokeStmt) unit).getInvokeExpr();
                        SootMethod invokedMethod = invokeExpr.getMethod();

                        // Check if the invoked method is from a subclass or superclass
                        if (isSubclassMethod(invokedMethod, sootClass, typeHierarchy)) {
                            System.out.println("Subclass method called: " + invokedMethod.getName());
                        } else {
                            System.out.println("Superclass method called: " + invokedMethod.getName());
                        }
                    }
                }
            }
        }
    }

    private static boolean isSubclassMethod(SootMethod invokedMethod, SootClass currentClass, Hierarchy typeHierarchy) {
        SootClass declaringClass = invokedMethod.getDeclaringClass();

        // Check if the invoking class is a subclass of the declaring class
        return typeHierarchy.isClassSubclassOf(currentClass, declaringClass);
    }
}