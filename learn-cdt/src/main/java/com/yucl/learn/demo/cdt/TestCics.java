package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDeclarator;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.core.runtime.CoreException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestCics {
    public static void main(String[] args) throws Exception {
        String sourcecode = "int a; void test() {a++;}";

        sourcecode = new String(Files.readAllBytes(Paths.get("L:\\workspaces\\IdeaProjects\\sourcecode\\c\\tbcreate.c")), "GBK");
        IASTTranslationUnit translationUnit = TestCics.getIASTTranslationUnit(sourcecode.toCharArray());

        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit) translationUnit;

       /* Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(iastDeclaration -> {
            System.out.println(iastDeclaration.getRawSignature());
        });*/

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTName name) {
                if (name.getParent() instanceof CPPASTFunctionDeclarator) {
                    System.out.println("IASTName: " + name.getClass().getSimpleName() + "(" + name.getRawSignature() + ") - > parent: " + name.getParent().getClass().getSimpleName());
                    //System.out.println("-- isVisible: " + ParserExample.isVisible(name));
                }
                // System.out.println(name.getLastName());
               /* if(name.isDeclaration()){
                    IBinding b = name.resolveBinding();
                     if(b instanceof IFunction){
                        // System.out.println(b.getName());
                     }
                }else if(name.isDefinition()){
                    IBinding b = name.resolveBinding();
                    if(b instanceof IFunction){
                        System.out.println(b.getName());
                    }
                }else if (name.isReference()) {
                    IBinding b = name.resolveBinding();
                    IType type = (b instanceof IFunction) ? ((IFunction) b).getType() : null;
                    if (type != null)
                        System.out.println("Referencing " + name + ", type " + ASTTypeUtil.getType(type));
                }*/
                return super.visit(name);
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                // When CDT visit a declaration
                if ((declaration instanceof IASTFunctionDefinition)) {
                    IASTFunctionDefinition ast = (IASTFunctionDefinition) declaration;
                    IScope scope = ast.getScope();
                    try {
                        System.out.println("### function() - Parent = " + scope.getParent().getScopeName());
                        System.out.println("### function() - Syntax = " + ast.getSyntax());
                    } catch (DOMException e) {
                        e.printStackTrace();
                    } catch (ExpansionOverlapsBoundaryException e) {
                        e.printStackTrace();
                    }
                    ICPPASTFunctionDeclarator typedef = (ICPPASTFunctionDeclarator) ast.getDeclarator();
                    System.out.println("------- typedef: " + typedef.getName());
                }

                return PROCESS_CONTINUE;
            }

            @Override
            public int leave(IASTDeclaration declaration) {

                return super.leave(declaration);
            }

            @Override
            public int visit(IASTStatement statement) {
                if (statement.getRawSignature().startsWith("CREATE")) {
                    // System.out.println(statement.getRawSignature());
                }

                return super.visit(statement);
            }

            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression = (IASTFunctionCallExpression) expression;
                    // expression.getTranslationUnit().getReferences(expre)
                    // System.out.println(callExpression.getFunctionNameExpression().getRawSignature());
                    //System.out.println(expression.getRawSignature() + "    " +  expression.getExpressionType());
                }


                //   System.out.println(expression.getRawSignature() + "    " + expression.getClass());

               /* if (expression instanceof IASTFunctionCallExpression) {
                    IASTExpression functionNameExpression = ((IASTFunctionCallExpression) expression).getFunctionNameExpression();
                    if (functionNameExpression instanceof IASTIdExpression) {
                        IASTIdExpression idExpression = (IASTIdExpression) functionNameExpression;
                        IBinding binding = idExpression.getName().resolveBinding();
                        ICProject cProject = expression.getTranslationUnit().getOriginatingTranslationUnit().getCProject();
                        IIndex index;
                        try {
                            index = CCorePlugin.getIndexManager().getIndex(cProject);
                            index.acquireReadLock();
                        } catch (CoreException e) {
                            // log exception??
                            return PROCESS_ABORT;
                        } catch (InterruptedException e) {
                            return PROCESS_ABORT;
                        }
                        try {
                            IIndexName[] names = index.findNames(binding, IIndex.FIND_DEFINITIONS);
                            if (names.length > 0) {
                                IIndexName definitionIndexName = names[0];
                                IIndexFileLocation indexFileLocation = definitionIndexName.getFile().getLocation();
                                //CCorePlugin.getDefault().getCoreModel().getCModel();
                                ITranslationUnit translationUnit = CoreModelUtil.findTranslationUnitForLocation(indexFileLocation, cProject);
                                IASTTranslationUnit astOtherFile = translationUnit.getAST(index, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
                                IASTNodeSelector selector = astOtherFile.getNodeSelector(null);
                                IASTNode node = selector.findEnclosingNode(definitionIndexName.getNodeOffset(), definitionIndexName.getNodeLength());
                                // This is probably going to always be a IASTName but maybe there are some other weird cases
                                if (node instanceof IASTName) {
                                    ASTNodeProperty propertyInParent = node.getPropertyInParent();
                                    // Maybe this is being too careful and the "instanceof IASTFunctionDeclarator" is enough?
                                    if (propertyInParent == IASTDeclarator.DECLARATOR_NAME) {
                                        IASTNode parent = node.getParent();
                                        if (parent instanceof IASTFunctionDeclarator) {
                                            IASTFunctionDeclarator functionDeclarator = (IASTFunctionDeclarator) parent;
                                            System.out.println("success!");
                                        }
                                    }
                                }
                            }
                        } catch (CoreException e) {
                            // log exception??
                            return PROCESS_ABORT;
                        } finally {
                            index.releaseReadLock();
                        }


                    }
                }*/

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

        String[] includeSearchPaths = new String[]{"l:\\workspaces\\IdeaProjects\\sourcecode\\include", "l:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include", "L:\\cdt\\include"};

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