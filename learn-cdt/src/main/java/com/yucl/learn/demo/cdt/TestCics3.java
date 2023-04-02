package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexFileLocation;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTProblemDeclaration;
import org.eclipse.cdt.internal.core.parser.IMacroDictionary;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContent;
import org.eclipse.cdt.internal.core.parser.scanner.InternalFileContentProvider;
import org.eclipse.cdt.internal.formatter.CCodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestCics3 {
    public static void main(String[] args) throws Exception {
        String sourcecode = "int a; void test() {a++;}";


        BufferedReader reader = new BufferedReader(new FileReader("D:\\jzjy\\c\\tbcreate.sqc"));
        String line = null;
        StringBuilder sql = new StringBuilder();
        StringBuilder fileBody = new StringBuilder();
        boolean find = false;
        int n=0;
        while ((line= reader.readLine())!= null){
            String trimLine = line.trim();
            if(trimLine.startsWith("EXEC") &&  trimLine.length()>4 && trimLine.substring(4).trim().startsWith("SQL")){
                System.out.println("find");
                sql.append(line);
                find = true;
                n= n +1;
            }
            else if(find){
                sql.append(line.trim());
            }
            if(!find){
              fileBody.append(line).append("\n");
            }
            if(trimLine.endsWith(";") && find){
                System.out.println(sql.toString());
                find = false;

                fileBody.append("char * __sql").append(n).append("=\"").append(sql).append("\";\n");
                sql= new StringBuilder();
            }
        }


     //   sourcecode = new String(Files.readAllBytes(Paths.get("D:\\jzjy\\c\\tbcreate.sqc")), StandardCharsets.UTF_8);
        sourcecode = fileBody.toString();
       // sourcecode.replaceAll("EXEC SQL\\w;","char* ___sql___=");


        org.eclipse.cdt.core.formatter.CodeFormatter formatter = new CCodeFormatter();


        TextEdit edit = formatter.format(0,sourcecode,0,sourcecode.length(),0,null);

        IDocument doc=new Document(sourcecode);
        edit.apply(doc);
        System.out.println(doc.get());

        IASTTranslationUnit translationUnit = TestCics3.getIASTTranslationUnit(sourcecode.toCharArray());


        ICPPASTTranslationUnit icppastTranslationUnit = (ICPPASTTranslationUnit)translationUnit;



        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTName name) {
                if (name.isReference()) {
                    IBinding b = name.resolveBinding();
                    if(b instanceof  IFunction){
                        IType type = ((IFunction) b).getType();
                      //  System.out.println("Referencing " + name + ", type " + ASTTypeUtil.getType(type));

                    }else if(b instanceof ICPPClassType){
                         //  ICPPClassType icppClassType=(ICPPClassType)b;
                        //  System.out.println(icppClassType);
                    }else{
                        // System.out.println(b.getName());
                    }
                }
                return super.visit(name);
            }

            @Override
            public int visit(IASTExpression expression) {
                if(expression instanceof IASTFunctionCallExpression) {
                    IASTFunctionCallExpression callExpression =   (IASTFunctionCallExpression)expression;
                    IType type =callExpression.getFunctionNameExpression().getExpressionType();
                    // expression.getTranslationUnit().getReferences(expre)
                      System.out.println("callExpression:"+callExpression.getFunctionNameExpression()+", type " + ASTTypeUtil.getType(type));
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

        // Enable CDT to visit declaration
        visitor.shouldVisitNames = true;
        visitor.shouldVisitDeclarations = true;
        visitor.shouldVisitExpressions = true;
        visitor.shouldVisitStatements = true;
        visitor.shouldVisitProblems = true;

        Arrays.stream(icppastTranslationUnit.getDeclarations()).forEach(declaration -> {
            if ((declaration instanceof IASTFunctionDefinition)) {

                IASTFunctionDefinition ast = (IASTFunctionDefinition) declaration;
                ast.getFileLocation().getStartingLineNumber();
                //System.out.println("*****************************");
                //System.out.println(ast.getRawSignature());

                Arrays.stream(ast.getChildren()).forEach(node->{
                    //System.out.println("################### <<<");
                   // System.out.println( node.getRawSignature());
                   // System.out.println("###################>>>>");
                });

               // System.out.println("*****************************");

                ICPPASTFunctionDeclarator typedef = (ICPPASTFunctionDeclarator) ast.getDeclarator();
                System.out.println("------- typedef: " + typedef.getName());


                ((IASTFunctionDefinition) declaration).getBody().accept(visitor);


            }else{
                if(declaration instanceof CPPASTProblemDeclaration){
                    CPPASTProblemDeclaration problemDeclaration = (CPPASTProblemDeclaration)declaration;
                    System.out.println(problemDeclaration.getFileLocation().getStartingLineNumber());
                    System.out.println(problemDeclaration.getProblem().getMessage());
                }

            }
        });






        // Adapt visitor with source code unit
       // icppastTranslationUnit.accept(visitor);
    }

    public static IASTTranslationUnit getIASTTranslationUnit(char[] code) throws Exception {
        FileContent fc = FileContent.create("", code);
        Map<String, String> macroDefinitions = new HashMap<>();
       // String[] includeSearchPaths = new String[]{"L:\\cdt\\llvm-mingw\\include","L:\\cdt\\llvm-mingw\\include\\c++\\v1","L:\\cdt\\include","D:\\tools\\tc2\\INCLUDE","l:\\workspaces\\IdeaProjects\\sourcecode\\include","l:\\workspaces\\IdeaProjects\\sourcecode\\db2\\include"};

        String[] includeSearchPaths = new String[]{"D:\\jzjy\\sourcecode\\include","D:\\jzjy\\db2include\\include","D:\\jzjy\\include\\include"};

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