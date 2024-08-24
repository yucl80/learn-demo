package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImproperExceptionHandlingOrderCheck implements Check {

    @Override
    public void performCheck(CompilationUnit cu, CheckResult result) {
        try {
            
            cu.findAll(CatchClause.class).forEach(catchClause -> {
                List<Type> caughtExceptions = new ArrayList<>();
                boolean improperOrder = false;

                for (CatchClause cc : cu.findAll(CatchClause.class)) {
                    Type exceptionType = cc.getParameter().getType();
                    for (Type prevType : caughtExceptions) {
                        if (isSubclass(exceptionType, prevType)) {
                            result.addIssue("Improper exception handling order at line " + cc.getBegin().get().line);
                            improperOrder = true;
                            break;
                        }
                    }
                    if (improperOrder) break;
                    caughtExceptions.add(exceptionType);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSubclass(Type subType, Type superType) {
        // Simplified subclass check; this would need a more detailed type hierarchy check
        return subType.toString().equals("Exception") && superType.toString().equals("RuntimeException");
    }
}
