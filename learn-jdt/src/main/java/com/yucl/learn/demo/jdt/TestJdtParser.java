package com.yucl.learn.demo.jdt;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestJdtParser {
    public static void main(String[] args) {
        String mavenHome = System.getProperty("user.home") + "/.m2";

        String jarPath =  "D:\\Java\\repository\\junit\\junit\\4.13.2\\junit-4.13.2.jar";


        String str = ""
                + "import org.junit.Assert;\n"
                + "import org.junit.Test;\n"
                + "\n"
                + "public class ATest {"
                + "	@Test\n"
                + "	public void test() throws Exception {\n"
                + "		Assert.assertTrue(1 == 1);"
                + "}\n";

        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setBindingsRecovery(true);

        Map<String, String> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        String unitName = "ATest.java";
        parser.setUnitName(unitName);

        String[] sources = { };
        String[] classpath = {jarPath};

        parser.setEnvironment(classpath, sources, new String[] {}, true);
        parser.setSource(str.toCharArray());

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        Set<String> names = new HashSet<>();

        cu.accept(new ASTVisitor() {

            @Override
            public boolean visit(MethodInvocation node) {
                SimpleName name = node.getName();
                System.out.println("> node: " + node);
                if ("assertTrue".contains(name.getIdentifier())) {
                    IMethodBinding methodBinding = node.resolveMethodBinding();
                    if(methodBinding!= null) {
                        System.out.println("> methodBinding: " + methodBinding);

                        String declaringClassQualifiedName = methodBinding.getDeclaringClass().getQualifiedName();
                        names.add(declaringClassQualifiedName);
                    }
                }
                return true;
            }
        });
        System.out.println(names);

    }
}
