package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;

import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.parser.DefaultLogService;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ScannerInfo;
import org.eclipse.core.runtime.CoreException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CDTFunctionCallChain {

    public static void main(String[] args) {
        String filePath = "path/to/your/cpp/file.cpp";
        try {
            ICPPASTTranslationUnit translationUnit = parseCppFile(filePath);
            if (translationUnit != null) {
                printFunctionCallChain(translationUnit);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private static ICPPASTTranslationUnit parseCppFile(String filePath) throws CoreException {
        FileContent fileContent = FileContent.createForExternalFileLocation(filePath);
        IScannerInfo scannerInfo = new ScannerInfo();
        IParserLogService log = new DefaultLogService();
        IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
        int opts = 8; // GNU C++
        return (ICPPASTTranslationUnit) GPPLanguage.getDefault().getASTTranslationUnit(fileContent, scannerInfo, emptyIncludes, null, opts, log);
    }

    private static void printFunctionCallChain(ICPPASTTranslationUnit translationUnit) {
        for (IASTNode node : translationUnit.getChildren()) {
            if (node instanceof ICPPASTFunctionDefinition) {
                ICPPASTFunctionDefinition functionDefinition = (ICPPASTFunctionDefinition) node;
                String functionName = getFunctionName(functionDefinition);
                System.out.println("Function: " + functionName);
                printFunctionCalls(functionDefinition.getBody(), functionName);
            }
        }
    }

    private static String getFunctionName(ICPPASTFunctionDefinition functionDefinition) {
        IASTDeclarator declarator = functionDefinition.getDeclarator();
        if (declarator.getName() instanceof ICPPASTQualifiedName) {
            ICPPASTQualifiedName qualifiedName = (ICPPASTQualifiedName) declarator.getName();
            return qualifiedName.toString();
        } else if (declarator.getName() instanceof ICPPASTName) {
            ICPPASTName name = (ICPPASTName) declarator.getName();
            return name.toString();
        }
        return "Unknown";
    }

    private static void printFunctionCalls(IASTNode node, String currentFunction) {
        for (IASTNode child : node.getChildren()) {
            if (child instanceof ICPPASTFunctionCallExpression) {
                ICPPASTFunctionCallExpression functionCall = (ICPPASTFunctionCallExpression) child;
                IASTExpression functionNameExpression = functionCall.getFunctionNameExpression();
                if (functionNameExpression instanceof ICPPASTName) {
                    ICPPASTName functionName = (ICPPASTName) functionNameExpression;
                    System.out.println("  " + currentFunction + " -> " + functionName.toString());
                }
            }
            printFunctionCalls(child, currentFunction);
        }
    }
}