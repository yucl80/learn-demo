package com.yucl.learn.demo.cdt.parser.c;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.c.*;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.core.runtime.CoreException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SqcParser {
    private static final Pattern PATTERN_EXEC_SQL = Pattern.compile("(?i)[\\s|\t]*EXEC[\\s\t]*SQL");
    private static final Pattern PATTERN_INCLUDE_SQLCA = Pattern.compile("(?i)EXEC[\\s|\t]*SQL[\\s|\t]*INCLUDE[\\s|\t]*SQLCA");

    public static void main(String[] args) {
        try {
            parse("L:\\workspaces\\IdeaProjects\\sourcecode\\c\\tbcreate.sqc");
        } catch (CoreException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void parse(String filePath) throws CoreException, IOException {
        String[] includeSearchPaths = new String[]{"L:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include", "L:\\workspaces\\IdeaProjects\\sourcecode\\include"};
        String sourceCode = readFile(filePath);

        IASTTranslationUnit translationUnit = getIASTTranslationUnit(filePath, sourceCode.toCharArray(), includeSearchPaths);
        CASTTranslationUnit castTranslationUnit = (CASTTranslationUnit) translationUnit;
        System.out.println(translationUnit.getClass());

        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression = (IASTFunctionCallExpression) expression;
                    IType type = callExpression.getFunctionNameExpression().getExpressionType();
                    // expression.getTranslationUnit().getReferences(expre)
                    String args = "   ;args:" + Arrays.stream(callExpression.getArguments()).map(Object::toString).collect(Collectors.joining(","));
                    IASTExpression functionNameExpression = callExpression.getFunctionNameExpression();
                    if (functionNameExpression instanceof CASTIdExpression) {
                        CASTIdExpression idExpression = (CASTIdExpression) functionNameExpression;
                        if (idExpression.getName().resolveBinding().getName().equals("callSql")) {
                            System.out.println("callExpression:" + ASTTypeUtil.getType(idExpression.getExpressionType()) + "     " + idExpression.getName().resolveBinding() + args);
                        }
                    } else if (functionNameExpression instanceof CASTFieldReference) {
                        CASTFieldReference castFieldReference = (CASTFieldReference) functionNameExpression;
                        System.out.println("call class function:" + castFieldReference.getFieldOwner() + "." + castFieldReference.getFieldName().resolveBinding() + args);

                    } else {
                        System.out.println("callExpression:" + functionNameExpression + ", type " + ASTTypeUtil.getType(type));
                    }
                }

                //System.out.println(expression.getRawSignature() + "    " + expression.getClass());
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


        if (castTranslationUnit.getFileLocation().getFileName().endsWith(".sqc")) {
            System.out.println(castTranslationUnit.getFileLocation().getFileName());
            Arrays.stream(castTranslationUnit.getDeclarations()).forEach(declaration -> {
                if (declaration instanceof IASTFunctionDefinition) {
                    // System.out.println(declaration.getClass());
                    ((IASTFunctionDefinition) declaration).getBody().accept(visitor);
                } else if (declaration instanceof CASTSimpleDeclaration) {
                    //System.out.println(declaration.getClass());
                    //  CASTSimpleDeclaration simpleDeclaration = (CASTSimpleDeclaration)declaration;
                    //simpleDeclaration.getDeclSpecifier().get
                } else if (declaration instanceof CASTProblemDeclaration) {
                    IASTProblem problem = ((CASTProblemDeclaration) declaration).getProblem();
                    System.out.println(problem.getMessage() + "  " + problem.getRawSignature());
                }
            });
        }
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


        return GCCLanguage.getDefault().getASTTranslationUnit(fc, si, fileContentProvider, null, ILanguage.OPTION_IS_SOURCE_UNIT, new DefaultLogService());
    }

    public static String readFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        StringBuilder sql = new StringBuilder();
        StringBuilder fileBody = new StringBuilder();
        boolean find = false;
        int n = 0;
        while ((line = reader.readLine()) != null) {
            String trimLine = line.trim();
            Matcher matcher2 = PATTERN_EXEC_SQL.matcher(trimLine);
            if (matcher2.find()) {
                sql.append("\"").append(line).append("\"\n");
                if (trimLine.endsWith(";")) {
                    Matcher matcher = PATTERN_INCLUDE_SQLCA.matcher(sql.toString());
                    if (matcher.find()) {
                        fileBody.append("void callSql(char * sql){}; \n");
                    } else {
                        fileBody.append("callSql(\"").append(line).append("\");\n");
                    }
                    sql = new StringBuilder();
                } else {
                    find = true;
                }
                n = n + 1;
            } else if (find) {
                sql.append("\"").append(line).append("\"");
                if (trimLine.endsWith(";")) {
                    find = false;
                    Matcher matcher = PATTERN_INCLUDE_SQLCA.matcher(sql.toString());
                    if (matcher.find()) {
                        fileBody.append("//").append(sql);
                    } else {
                        fileBody.append("callSql(").append(sql).append(");\n");
                    }
                    sql = new StringBuilder();
                } else {
                    sql.append("\n");
                }

            } else {
                fileBody.append(line).append("\n");
            }
        }
        String body = fileBody.toString();//+ " void  callSql(char* sql){ ;}\n";
        // System.out.println(body);
        return body;
    }
}

