package com.yucl.learn.demo.jdt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;
import org.junit.Test;

/**
 * A complete standalone example of ASTParser
 * @see https://www.programcreek.com/2011/01/a-complete-standalone-example-of-astparser/
 *
 */
public class StandaloneParserTest {

  //  @Test
    public void test() throws Exception {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource((""
                + "public class A {"
                + "  int i = 9;\n"
                + "  int j;\n"
                + "  ArrayList<Integer> al = new ArrayList<Integer>();\n"
                + "  \n"
                + "  public void main() {\n"
                + "    j=1000;\n"
                + "  }\n"
                + "}\n").toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        Set<String> names = new HashSet<>();
        cu.accept(new ASTVisitor() {

            public boolean visit(VariableDeclarationFragment node) {
                SimpleName name = node.getName();
                names.add(name.getIdentifier());
                System.out.println("Declaration of '" + name + "' at line " + cu.getLineNumber(name.getStartPosition()));
                return true; // set to 'false' to not visit usage info
            }

            public boolean visit(SimpleName node) {
                if (names.contains(node.getIdentifier())) {
                    System.out.println("Usage of '" + node + "' at line " + cu.getLineNumber(node.getStartPosition()));
                }
                return true;
            }

            @Override
            public boolean visit(MethodDeclaration node) {
                return super.visit(node);
            }

            @Override
            public boolean visit(MethodInvocation node) {
                System.out.println(node.getName());
                return super.visit(node);
            }
        });

        assertEquals(3, names.size());
        assertTrue(names.contains("i"));
        assertTrue(names.contains("j"));
        assertTrue(names.contains("al"));
    }
}
