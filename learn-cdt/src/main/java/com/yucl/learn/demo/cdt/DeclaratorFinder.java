package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;


/**
 * Given a selection and a translation unit, this class finds a
 * ICPPASTFunctionDeclarator if possible. Special case: Nested local functions
 * are skipped during search.
 */
public class DeclaratorFinder {

    private IASTFunctionDeclarator foundDeclarator;

    public IASTName getName() {
        return foundDeclarator.getName();
    }

    private IASTFunctionDeclarator findDeclaratorInAncestors(IASTNode node) {
        while (node != null) {
            IASTFunctionDeclarator declarator = extractDeclarator(node);
            if (node instanceof ICPPASTTemplateDeclaration) {
                declarator = extractDeclarator(((ICPPASTTemplateDeclaration) node).getDeclaration());
            }
            if (declarator != null) {
                return declarator;
            }
            node = node.getParent();
        }
        return null;
    }

    private IASTFunctionDeclarator extractDeclarator(IASTNode node) {
        if (node instanceof ICPPASTTemplateDeclaration) {
            node = ((ICPPASTTemplateDeclaration) node).getDeclaration();
        }
        if (node instanceof IASTFunctionDeclarator) {
            return (IASTFunctionDeclarator) node;
        }
        if (node instanceof IASTFunctionDefinition) {
            return ((IASTFunctionDefinition) node).getDeclarator();
        }
        if (node instanceof IASTSimpleDeclaration) {
            IASTDeclarator[] declarators = ((IASTSimpleDeclaration) node).getDeclarators();
            if (declarators.length == 1 &&
                    declarators[0] instanceof IASTFunctionDeclarator)
                return (IASTFunctionDeclarator) declarators[0];
        }
        return null;
    }


}
