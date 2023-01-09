package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionCallExpression;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CDTParser {
    public static void main(String[] args) throws Exception {
        String sourcecode = "int a; void test() {a++;}";
        sourcecode = new String(Files.readAllBytes(Paths.get("D:\\workspaces\\IdeaProjects\\learn-demo\\learn-cdt\\cpp\\lambda.cpp")));
        IASTTranslationUnit translationUnit = CDTParser.getIASTTranslationUnit(sourcecode.toCharArray());

        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit)translationUnit;

       /* Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(iastDeclaration -> {
            System.out.println(iastDeclaration.getRawSignature());
        });*/

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTDeclaration declaration) {
                // When CDT visit a declaration
               // System.out.println("Found a declaration: " + declaration.getRawSignature());

                return PROCESS_CONTINUE;
            }

            @Override
            public int leave(IASTDeclaration declaration) {
                return super.leave(declaration);
            }

            @Override
            public int visit(IASTStatement statement) {
               // System.out.println(statement.getTranslationUnit().getRawSignature());
                return super.visit(statement);
            }

            @Override
            public int visit(IASTExpression expression) {
                if(expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression =   (IASTFunctionCallExpression)expression;
                   // expression.getTranslationUnit().getReferences(expre)
                  //  System.out.println(callExpression.getFunctionNameExpression().getRawSignature());
                    System.out.println(expression.getRawSignature() + "    " +  expression.getExpressionType());
                }


                //   System.out.println(expression.getRawSignature() + "    " + expression.getClass());

                return super.visit(expression);
            }

            @Override
            public int leave(IASTExpression expression) {
                return super.leave(expression);
            }
        };




        // Enable CDT to visit declaration
        visitor.shouldVisitNames = true;
        visitor.shouldVisitDeclarations = true;
        visitor.shouldVisitExpressions = true;
        visitor.shouldVisitStatements = true;
        // Adapt visitor with source code unit
        icppastTranslationUnit.accept(visitor);
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