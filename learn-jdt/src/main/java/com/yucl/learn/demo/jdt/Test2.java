package com.yucl.learn.demo.jdt;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import java.util.ArrayList;
import java.util.List;

public class Test2 {

    public static void main(String[] args) {
        // First of all, get all the type declaration of the class.

    }

    public void f(IType unit ) throws JavaModelException {
        IType [] typeDeclarationList = unit.getTypes();

        for (IType typeDeclaration : typeDeclarationList) {
            // get methods list
            IMethod[] methodList = typeDeclaration.getMethods();

            for (IMethod method : methodList) {
                final List<String> referenceList = new ArrayList<String>();
                // check each method.
                String methodName = method.getElementName();
                if (!method.isConstructor()) {
                    // Finds the references of the method and record references of the method to referenceList parameter.
                   // JDTSearchProvider.searchMethodReference(referenceList, method, scope, iJavaProject);
                }
            }
        }
    }

}
