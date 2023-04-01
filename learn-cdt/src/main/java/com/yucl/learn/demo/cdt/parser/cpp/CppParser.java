package com.yucl.learn.demo.cdt.parser.cpp;

import com.yucl.learn.demo.cdt.TestCics4;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.core.runtime.CoreException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CppParser {
    public static void main(String[] args) {
        try {
            parse("learn-cdt/cpp/lambda.cpp");
        } catch (CoreException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void parse(String filePath) throws CoreException, IOException {
        String[] includeSearchPaths = new String[]{"L:\\cdt\\include", "l:\\workspaces\\IdeaProjects\\sourcecode\\include", "l:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include"};
        String sourceCode = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        IASTTranslationUnit translationUnit = getIASTTranslationUnit(sourceCode.toCharArray(), includeSearchPaths);
        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit) translationUnit;


        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTName name) {
                if (name.isReference()) {
                    IBinding b = name.resolveBinding();
                    if (b instanceof IFunction) {
                        IType type = ((IFunction) b).getType();
                        //  System.out.println("Referencing " + name + ", type " + ASTTypeUtil.getType(type));

                    } else if (b instanceof ICPPClassType) {
                        //  ICPPClassType icppClassType=(ICPPClassType)b;
                        //  System.out.println(icppClassType);
                    } else {
                        // System.out.println(b.getName());
                    }
                }
                return super.visit(name);
            }

            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression = (IASTFunctionCallExpression) expression;
                    IType type = callExpression.getFunctionNameExpression().getExpressionType();
                    // expression.getTranslationUnit().getReferences(expre)
                    System.out.println("callExpression:" + callExpression.getFunctionNameExpression() + ", type " + ASTTypeUtil.getType(type));
                    //System.out.println(expression.getRawSignature() + "    " +  expression.getExpressionType());
                }


                //   System.out.println(expression.getRawSignature() + "    " + expression.getClass());

                return super.visit(expression);
            }

            @Override
            public int visit(IASTStatement statement) {

                return super.visit(statement);
            }
        };

        visitor.shouldVisitNames = true;
        visitor.shouldVisitDeclarations = true;
        visitor.shouldVisitExpressions = true;
        visitor.shouldVisitStatements = true;
        visitor.shouldVisitProblems = true;

       // if (icppastTranslationUnit.getFileLocation().getFileName().endsWith(".cpp")) {
            Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(iastDeclaration -> {
                if (iastDeclaration instanceof CPPASTSimpleDeclaration) {
                    CPPASTCompositeTypeSpecifier compositeTypeSpecifier = (CPPASTCompositeTypeSpecifier) ((CPPASTSimpleDeclaration) iastDeclaration).getDeclSpecifier();
                    IBinding iBinding = compositeTypeSpecifier.getName().getBinding();
                    if (iBinding instanceof ICPPClassType) {
                        ICPPClassType classType = (ICPPClassType) iBinding;
                        System.out.println("class:" + ASTTypeUtil.getType(classType));
                        Arrays.stream(classType.getDeclaredMethods()).forEach(icppMethod -> {
                            System.out.println("method:" + icppMethod.getName());
                        });
                        /*Arrays.stream(classType.getAllDeclaredMethods()).forEach(icppMethod -> {
                            System.out.println("all method:" + icppMethod.getName());
                        });*/
                        Arrays.stream(classType.getFields()).forEach(iField -> {
                            System.out.println("field:"+iField.getName());
                        });
                    }
                    System.out.println();
                }
            });

      //  }
    }

    public static IASTTranslationUnit getIASTTranslationUnit(char[] code, String[] includeSearchPaths) throws CoreException {
        FileContent fc = FileContent.create("", code);
        Map<String, String> macroDefinitions = new HashMap<>();

        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        // IncludeFileContentProvider emptyFilesProvider = IncludeFileContentProvider.getEmptyFilesProvider();
        IncludeFileContentProvider fileContentProvider = new InternalFileContentProvider() {
            @Override
            public InternalFileContent getContentForInclusion(String filePath, IMacroDictionary macroDictionary) {

                return (InternalFileContent) FileContent.createForExternalFileLocation(filePath);
            }

            @Override
            public InternalFileContent getContentForInclusion(IIndexFileLocation ifl, String astPath) {

                return (InternalFileContent) FileContent.create(ifl);
            }
        };

        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, fileContentProvider, null, ILanguage.OPTION_IS_SOURCE_UNIT, new DefaultLogService());
    }

}
