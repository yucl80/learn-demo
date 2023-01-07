package com.yucl.learn.demo.jdt;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class ASTTester {
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\pc\\workspace\\asttester\\src\\test\\Apple.java";
        File file = new File(path);
        String str = new String(Files.readAllBytes(Paths.get(path)));
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setBindingsRecovery(true);
        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        String unitName = "Apple.java";
        parser.setUnitName(unitName);
        String[] sources = {"C:\\Users\\pc\\workspace\\asttester\\src"};
        String[] classpath = {"C:\\Program Files\\Java\\jre1.8.0_25\\lib\\rt.jar"};
        parser.setEnvironment(classpath, sources, new String[]{"UTF-8"}, true);
        parser.setSource(str.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        if (cu.getAST().hasBindingsRecovery()) {
            System.out.println("Binding activated.");
        }
        TypeFinderVisitor v = new TypeFinderVisitor();
        cu.accept(v);
    }
}

class TypeFinderVisitor extends ASTVisitor {
    public boolean visit(VariableDeclarationStatement node) {
        for (Iterator iter = node.fragments().iterator(); iter.hasNext(); ) {
            System.out.println("------------------");
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) iter.next();
            IVariableBinding binding = fragment.resolveBinding();
            System.out.println("binding variable declaration: " + binding.getVariableDeclaration());
            System.out.println("binding: " + binding);
        }
        return true;
    }
}