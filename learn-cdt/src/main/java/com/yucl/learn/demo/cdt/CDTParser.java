package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CDTParser {
    public static void main(String[] args) throws Exception {
        String sourcecode = "int a; void test() {a++;}";
        sourcecode = new String(Files.readAllBytes(Paths.get("d:/tmp/code.h")));
        IASTTranslationUnit translationUnit = CDTParser.getIASTTranslationUnit(sourcecode.toCharArray());

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTDeclaration declaration) {
                // When CDT visit a declaration
               // System.out.println("Found a declaration: " + declaration.getRawSignature());
                return PROCESS_CONTINUE;
            }

            @Override
            public int visit(IASTStatement statement) {
                System.out.println(statement);
                return super.visit(statement);
            }


        };
        // Enable CDT to visit declaration
        visitor.shouldVisitDeclarations = true;
        // Adapt visitor with source code unit
        translationUnit.accept(visitor);
    }

    public static IASTTranslationUnit getIASTTranslationUnit(char[] code) throws Exception {
        FileContent fc = FileContent.create("", code);
        Map<String, String> macroDefinitions = new HashMap<>();
        String[] includeSearchPaths = new String[0];
        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
    }
}