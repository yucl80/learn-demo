package com.yucl.learn.demo.cdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.CPPASTVisitor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTName;


@SuppressWarnings("restriction")
public class InsertionPointFinder {

    private static ArrayList<ICPPASTFunctionDeclarator> allafterdeclarations;
    private static ArrayList<ICPPASTFunctionDefinition> alldefinitionsoutside;
    private static IASTDeclaration position;

    public static IASTDeclaration findInsertionPoint(IASTTranslationUnit classunit, IASTTranslationUnit functiondefunit, IASTFunctionDeclarator funcdecl) {
        position = null;
        findAllDeclarationsAfterInClass(classunit, funcdecl);
        findAllDefinitionsoutSideClass(functiondefunit);
        findRightPlace();
        return position;
    }

    private static void findRightPlace() {
        if (allafterdeclarations == null || alldefinitionsoutside == null)
            return;
        for(ICPPASTFunctionDeclarator decl: allafterdeclarations) {
            String decl_name = decl.getName().toString();
            for(ICPPASTFunctionDefinition def: alldefinitionsoutside) {
                String def_name = null;
                if (def.getDeclarator().getName() instanceof ICPPASTQualifiedName) {
                    ICPPASTQualifiedName qname = (ICPPASTQualifiedName) def.getDeclarator().getName();
                    def_name = qname.getLastName().toString();
                }
                else if (def.getDeclarator().getName() instanceof CPPASTName) {
                    def_name = def.getDeclarator().getName().toString();
                }

                if (decl_name.equals(def_name)) {
                    if (def.getParent() != null && def.getParent() instanceof ICPPASTTemplateDeclaration)
                        position = (IASTDeclaration) def.getParent();
                    else
                        position = def;
                    return;
                }
            }
        }
    }

    private static void findAllDeclarationsAfterInClass(IASTTranslationUnit classunit, IASTFunctionDeclarator funcdecl) {
        ICPPASTCompositeTypeSpecifier klass = getklass(classunit);
        if (klass != null)
            allafterdeclarations = getDeclarationsInClass(klass, funcdecl);
    }

    /**
     * @param unit, the translation unit where to find the definitions
     */
    private static void findAllDefinitionsoutSideClass(IASTTranslationUnit unit) {
        final ArrayList<ICPPASTFunctionDefinition> definitions = new ArrayList<ICPPASTFunctionDefinition>();
        if (unit == null) {
            alldefinitionsoutside = definitions;
            return;
        }
        unit.accept(
                new CPPASTVisitor() {
                    {
                        shouldVisitDeclarations = true;
                    }

                    @Override
                    public int visit(IASTDeclaration declaration) {
                        if (declaration instanceof ICPPASTFunctionDefinition) {
                            if (declaration.getParent() != null && ToggleNodeHelper.getAncestorOfType(declaration, CPPASTCompositeTypeSpecifier.class) != null)
                                return PROCESS_CONTINUE;
                            definitions.add((ICPPASTFunctionDefinition) declaration);
                        }
                        return super.visit(declaration);
                    }
                });
        alldefinitionsoutside = definitions;
    }

    private static ArrayList<ICPPASTFunctionDeclarator> getDeclarationsInClass(ICPPASTCompositeTypeSpecifier klass, final IASTFunctionDeclarator selected) {
        final ArrayList<ICPPASTFunctionDeclarator> declarations = new ArrayList<ICPPASTFunctionDeclarator>();

        klass.accept(
                new CPPASTVisitor() {
                    {
                        shouldVisitDeclarators = true;
                    }

                    boolean got = false;
                    @Override
                    public int visit(IASTDeclarator declarator) {
                        if (declarator instanceof ICPPASTFunctionDeclarator) {
                            if (((ICPPASTFunctionDeclarator) declarator) == selected) {
                                got = true;
                            }
                            if (got) {
                                declarations.add((ICPPASTFunctionDeclarator) declarator);
                            }
                        }
                        return super.visit(declarator);
                    }
                });

        return declarations;
    }

    private static ICPPASTCompositeTypeSpecifier getklass(IASTTranslationUnit unit) {
        final List<ICPPASTCompositeTypeSpecifier> result = new ArrayList<ICPPASTCompositeTypeSpecifier>();

        unit.accept(
                new CPPASTVisitor() {
                    {
                        shouldVisitDeclSpecifiers = true;
                    }

                    @Override
                    public int visit(IASTDeclSpecifier declSpec) {
                        if (declSpec instanceof ICPPASTCompositeTypeSpecifier) {
                            result.add((ICPPASTCompositeTypeSpecifier) declSpec);
                            return PROCESS_ABORT;
                        }
                        return super.visit(declSpec);
                    }
                });
        return result.get(0);
    }
}
