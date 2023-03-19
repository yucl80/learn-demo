package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestCics {
    public static void main(String[] args) throws Exception {
        String sourcecode = "int a; void test() {a++;}";

        sourcecode = new String(Files.readAllBytes(Paths.get("L:\\workspaces\\IdeaProjects\\sourcecode\\comnfunc\\bankbus.c")),"GBK");
        IASTTranslationUnit translationUnit = TestCics.getIASTTranslationUnit(sourcecode.toCharArray());

        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit)translationUnit;

       /* Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(iastDeclaration -> {
            System.out.println(iastDeclaration.getRawSignature());
        });*/

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTDeclaration declaration) {
                // When CDT visit a declaration
              //  System.out.println("Found a declaration: " + declaration.getRawSignature());

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
       // String[] includeSearchPaths = new String[]{"L:\\cdt\\llvm-mingw\\include","L:\\cdt\\llvm-mingw\\include\\c++\\v1","L:\\cdt\\include","D:\\tools\\tc2\\INCLUDE","l:\\workspaces\\IdeaProjects\\sourcecode\\include","l:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include"};

        String[] includeSearchPaths = new String[]{"l:\\workspaces\\IdeaProjects\\sourcecode\\include","l:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include","L:\\cdt\\include"};

        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider emptyFilesProvider = IncludeFileContentProvider.getEmptyFilesProvider();

        IncludeFileContentProvider fileContentProvider = new InternalFileContentProvider() {
            @Override
            public InternalFileContent getContentForInclusion(String filePath, IMacroDictionary macroDictionary) {
               /* InternalFileContent ifc = null;
                if (!shouldScanInclusionFiles) {
                    ifc =  new InternalFileContent(filePath, InternalFileContent.InclusionKind.SKIP_FILE);
                }else {
                    ifc = FileCache.getInstance().get(filePath);
                }
                if (ifc == null) {
                    ifc = (InternalFileContent) FileContent.createForExternalFileLocation(filePath);
                    FileCache.getInstance().put(filePath, ifc);
                }*/

                return (InternalFileContent) FileContent.createForExternalFileLocation(filePath);
            }

            @Override
            public InternalFileContent getContentForInclusion(IIndexFileLocation ifl, String astPath) {
                /*InternalFileContent c = FileCache.getInstance().get(ifl);
                if (c == null) {
                    c = (InternalFileContent) FileContent.create(ifl);
                    FileCache.getInstance().put(ifl, c);
                }*/
                return (InternalFileContent) FileContent.create(ifl);
            }
        };


        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, fileContentProvider, idx, options, log);
    }



}