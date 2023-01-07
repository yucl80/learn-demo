package com.yucl.learn.demo.cdt;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    private static IASTTranslationUnit parse(char[] code) throws Exception {
        FileContent fc = FileContent.create("/Path/ToResolveIncludePaths.cpp", code);
        Map<String, String> macroDefinitions = new HashMap<String, String>();
        String[] includeSearchPaths = new String[0];
        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
    }

    public static void main(String[] args) throws Exception {
        String code = "typedef float myType; void f(int i) {} void f(double d) {} " +
                "void main() { myType var = 4; f(var); }";
        IASTTranslationUnit translationUnit = parse(code.toCharArray());
        ASTVisitor visitor = new ASTVisitor() {
            @Override
            public int visit(IASTName name) {
                // Looking only for references, not declarations
                if (name.isReference()) {
                    IBinding b = name.resolveBinding();
                    IType type = (b instanceof IFunction) ? ((IFunction) b).getType() : null;
                    if (type != null)
                        System.out.print("Referencing " + name + ", type " + ASTTypeUtil.getType(type));
                }
                return ASTVisitor.PROCESS_CONTINUE;
            }
        };
        visitor.shouldVisitNames = true;
        translationUnit.accept(visitor);
    }
}
