package com.yucl.learn.demo.cdt.parser.cpp;

import com.yucl.learn.demo.cdt.TestCics4;
import com.yucl.learn.demo.cdt.parser.Utils;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
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
import java.util.stream.Collectors;

public class CppParser {
    public static void main(String[] args) {
        try {
            parse("L:\\workspaces\\IdeaProjects\\learn-demo\\learn-cdt\\cpp\\main.cpp");
        } catch (CoreException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void parse(String filePath) throws CoreException, IOException {
       // String[] includeSearchPaths = new String[]{ "/usr/include", "/usr/include/c++/4.8.2","/usr/lib/gcc/x86_64-redhat-linux/4.8.2/include","/usr/include/c++/4.8.2/backward","/usr/include/c++/4.8.2/x86_64-redhat-linux","/usr/local/include"};
        String[] includeSearchPaths = new String[]{ "L:\\cygwin64\\lib\\gcc\\x86_64-pc-cygwin\\11\\include","L:\\cygwin64\\lib\\gcc\\x86_64-pc-cygwin\\11\\include\\c++","L:\\cygwin64\\lib\\gcc\\x86_64-pc-cygwin\\11\\include\\c++\\backward","L:\\cygwin64\\lib\\gcc\\x86_64-pc-cygwin\\11\\include\\c++\\x86_64-pc-cygwin","L:\\cygwin64\\usr\\include\\w32api","L:\\cygwin64\\usr\\include\\c++\\v1","L:\\cdt\\linux\\include", "L:\\cdt\\linux\\include\\c++\\4.8.2"};
        String sourceCode = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        IASTTranslationUnit translationUnit = getIASTTranslationUnit(filePath, sourceCode.toCharArray(), includeSearchPaths);
        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit) translationUnit;

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression = (IASTFunctionCallExpression) expression;
                    IType type = callExpression.getFunctionNameExpression().getExpressionType();
                    // expression.getTranslationUnit().getReferences(expre)
                    String args = "   ;args:" + Arrays.stream(callExpression.getArguments()).map(Object::toString).collect(Collectors.joining(","));
                    IASTExpression functionNameExpression = callExpression.getFunctionNameExpression();
                    if (functionNameExpression instanceof CPPASTFieldReference) {
                        CPPASTFieldReference cppastFieldReference = (CPPASTFieldReference) functionNameExpression;
                        System.out.println("call class function:" + cppastFieldReference.getFieldOwnerType() + "." + cppastFieldReference.getFieldName().resolveBinding() + args);


                    } else if (functionNameExpression instanceof CPPASTLambdaExpression) {
                        CPPASTLambdaExpression lambdaExpression = (CPPASTLambdaExpression) functionNameExpression;
                        System.out.println("call lambdaExpression:" + lambdaExpression.getRawSignature() + args);
                    } else if (functionNameExpression instanceof CPPASTIdExpression) {
                        CPPASTIdExpression idExpression = (CPPASTIdExpression) functionNameExpression;

                        System.out.println("callExpression:" + ASTTypeUtil.getType(idExpression.getExpressionType()) + "     " + idExpression.getName().resolveBinding() + args);
                    } else {
                        System.out.println("callExpression:" + functionNameExpression + ", type " + ASTTypeUtil.getType(type));
                    }

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


        if (icppastTranslationUnit.getFileLocation().getFileName().endsWith(".cpp")) {
            System.out.println(icppastTranslationUnit.getFileLocation().getFileName());
            Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(iastDeclaration -> {
                if (iastDeclaration instanceof CPPASTSimpleDeclaration) {
                    CPPASTSimpleDeclaration simpleDeclaration = (CPPASTSimpleDeclaration) iastDeclaration;
                    IASTDeclSpecifier declSpecifier = simpleDeclaration.getDeclSpecifier();
                    if (declSpecifier instanceof ICPPASTCompositeTypeSpecifier) {
                        ICPPASTCompositeTypeSpecifier compositeTypeSpecifier = (ICPPASTCompositeTypeSpecifier) declSpecifier;
                        IBinding iBinding = compositeTypeSpecifier.getName().getBinding();
                        if (iBinding instanceof ICPPClassType) {
                            ICPPClassType classType = (ICPPClassType) iBinding;
                            // System.out.println("class:" + ASTTypeUtil.getType(classType));
                            Arrays.stream(classType.getBases()).forEach(icppBase -> {
                                // System.out.println("baseClass:" + icppBase.getBaseClass().getName());
                            });


                            Arrays.stream(classType.getDeclaredMethods()).forEach(icppMethod -> {

                                ICPPFunctionType declaredType = icppMethod.getDeclaredType();
                                String pararmType = Arrays.stream(declaredType.getParameterTypes()).map(paramType -> {
                                    if (paramType instanceof CPPTypedef) {
                                        return ((CPPTypedef) paramType).getName();
                                    } else {
                                        return paramType.toString();
                                    }
                                }).collect(Collectors.joining(","));
                                String methodSignature = icppMethod.getOwner() + "." + icppMethod.getName() + "(" + pararmType + ")"; //ASTTypeUtil.getType(icppMethod.getType()

                                // System.out.println("owner:"  + icppMethod.getOwner());

                                ICPPASTFunctionDeclarator cppFunction = ((CPPMethod) icppMethod).getDefinition();
                                if (cppFunction != null) {
                                    IASTNode astNode = cppFunction.getParent();
                                    astNode.accept(visitor);
                                    System.out.println("function define: " + methodSignature + " body hashcode:" + Utils.getFunctionBodyHashCode(astNode.getRawSignature()));
                                } else {
                                     /*if (icppMethod instanceof CPPConstructor) {
                                         CPPConstructor cppConstructor = (CPPConstructor) icppMethod;
                                         cppConstructor.getPrimaryDeclaration().accept(visitor);
                                         System.out.println("getRawSignature " + icppMethod.getOwner().getName() + "  ;  " + cppConstructor.getDefinition());
                                     } else {
                                         System.out.println("icppMethod  " + icppMethod.getClass());
                                         System.out.println("::::" + icppMethod.isDestructor());
                                     }*/
                                }

                            });

                            Arrays.stream(classType.getConstructors()).forEach(cppConstructor -> {
                                // System.out.println("cppConstructor " + cppConstructor.getName() + "   "+ cppConstructor.getOwner().getName());

                            });

                        /*Arrays.stream(classType.getAllDeclaredMethods()).forEach(icppMethod -> {
                            System.out.println("all method:" + icppMethod.getName());
                        });*/
                            Arrays.stream(classType.getDeclaredFields()).forEach(iField -> {
                                IType type = iField.getType();
                                String typeName;
                                if (type instanceof CPPTypedef) {
                                    typeName = ((CPPTypedef) type).getName();
                                } else {
                                    typeName = type.toString();
                                }
                                // System.out.println("field:" + iField.getName() + "," + typeName);
                            });

                        }
                        System.out.println();
                    } else {
                        // CPPASTSimpleDeclaration simpleDeclaration1=(CPPASTSimpleDeclaration)iastDeclaration;
                        // System.out.println(simpleDeclaration1.getDeclSpecifier() + "   "+ iastDeclaration.getClass()  );
                    }
                } else if (iastDeclaration instanceof CPPASTFunctionDefinition) {
                    CPPASTFunctionDefinition functionDefinition = (CPPASTFunctionDefinition) iastDeclaration;
                   // System.out.println(functionDefinition.getDeclarator().getName() + "************begin************" + functionDefinition.getScope().getScopeName());
                    //functionDefinition.accept(visitor);
                    functionDefinition.getBody().accept(visitor);


                    CPPASTFunctionDeclarator cppastFunctionDeclarator  = (CPPASTFunctionDeclarator) ((CPPASTFunctionDefinition) iastDeclaration).getDeclarator();

                    ICPPASTParameterDeclaration[] parameterDeclarations = cppastFunctionDeclarator.getParameters();
                    String paramTypes = Arrays.stream(parameterDeclarations).map(parameterDeclaration -> {
                        IType paramType = ((CPPParameter) parameterDeclaration.getDeclarator().getName().resolveBinding()).getType();
                        if (paramType instanceof CPPTypedef) {
                            return ((CPPTypedef) paramType).getName();
                        } else {
                            return paramType.toString();
                        }
                    }).collect(Collectors.joining(","));

                    String methodSignature= functionDefinition.getDeclarator().getName()+"("+ paramTypes+")";

                    System.out.println("function define: " + methodSignature + " body hashcode:" + Utils.getFunctionBodyHashCode(functionDefinition.getRawSignature()));
                } else {
                    //System.out.println(iastDeclaration.getClass());
                }
            });
        }

        //  }
    }

    public static IASTTranslationUnit getIASTTranslationUnit(String filePath, char[] code, String[] includeSearchPaths) throws CoreException {
        FileContent fc = FileContent.create(filePath, code);
        Map<String, String> macroDefinitions = new HashMap<>();

        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);

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

        // IncludeFileContentProvider emptyFilesProvider = IncludeFileContentProvider.getEmptyFilesProvider();

        int options = GPPLanguage.OPTION_IS_SOURCE_UNIT | ITranslationUnit.AST_SKIP_ALL_HEADERS; //ILanguage.OPTION_IS_SOURCE_UNIT

        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, fileContentProvider, null, ILanguage.OPTION_IS_SOURCE_UNIT, new DefaultLogService());
    }

}
