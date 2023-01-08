package com.yucl.learn.demo.jdt;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test2 {

    public static void main(String[] args) throws IOException {
        String file = "D:\\workspaces\\IdeaProjects\\learn-demo\\demoproject\\src\\main\\java\\com\\example\\demo\\ServicA.java";
        ASTParser parser = ASTParser.newParser(AST.JLS18);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);


        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_SOURCE, "1.8");
        parser.setCompilerOptions(options);

        String unitName = "ServicA.java";
        parser.setUnitName(unitName);

        String[] sources = {"D:\\workspaces\\IdeaProjects\\learn-demo\\demoproject\\src\\main\\java"};
        String[] classpath = {"D:\\Java\\jdk1.8.0_202\\jre\\lib\\rt.jar"};

        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);

        String str = new String(Files.readAllBytes(Paths.get(file)));

        parser.setSource(str.toCharArray());

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);





        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodDeclaration node) {
                System.out.println(node.resolveBinding());
                return super.visit(node);
            }

            @Override
            public void endVisit(MethodDeclaration node) {
                System.out.println();
                System.out.println();
                System.out.println();
                super.endVisit(node);
            }

            @Override
            public boolean visit(TypeMethodReference node) {
                System.out.println(node.resolveMethodBinding());
                return super.visit(node);
            }

            @Override
            public boolean visit(MethodRef node) {
                System.out.println("MethodRef " + node.resolveBinding());
                return super.visit(node);
            }

            @Override
            public boolean visit(ExpressionMethodReference node) {
                System.out.println("   " + node.resolveMethodBinding());
                return super.visit(node);
            }

            @Override
            public boolean visit(LambdaExpression node) {
                // System.out.println("LambdaExpression   "+node.resolveMethodBinding());

                return super.visit(node);
            }

            @Override
            public boolean visit(MethodInvocation node) {
                IMethodBinding methodBinding = node.resolveMethodBinding();

                Expression exp = node.getExpression();

                ITypeBinding typeBinding = exp.resolveTypeBinding();


                if (exp instanceof SimpleName) {

                    // System.out.println(((SimpleName) exp).getIdentifier());
                }

                String qn = "";
                if (typeBinding != null) {
                    qn = typeBinding.getQualifiedName();
                    String declaringClassQualifiedName = methodBinding.getDeclaringClass().getQualifiedName();
                }
                System.out.println("    " + node.getName().getFullyQualifiedName() + "   " + node.getExpression() + " : " + methodBinding + "    " + qn);

                return super.visit(node);
            }


            @Override
            public boolean visit(FieldAccess node) {
                System.out.println(node.getName().resolveBinding());
                return super.visit(node);
            }

            @Override
            public boolean visit(VariableDeclarationStatement node) {
                System.out.println("VariableDeclarationStatement  "+ node);
                return super.visit(node);
            }

            @Override
            public boolean visit(FieldDeclaration node) {
                System.out.println("FieldDeclaration  " + node);
                return super.visit(node);
            }
            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();

               // System.out.println("Declaration of '" + name + "' at line " + cu.getLineNumber(name.getStartPosition()));
                return true; // set to 'false' to not visit usage info
            }

            public boolean visit(SimpleName node) {

              //      System.out.println("Usage of '" + node +"(" + node.getFullyQualifiedName()+")' at line " + cu.getLineNumber(node.getStartPosition()));

                return true;
            }

        });

    }

    public void f(IType unit) throws JavaModelException {
        IType[] typeDeclarationList = unit.getTypes();

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
