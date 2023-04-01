package com.yucl.learn.demo.cdt.parser.cpp;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;

public class CppASTVisitor extends ASTVisitor {
    @Override
    public int visit(IASTName name) {
        if (name.isReference()) {
            IBinding b = name.resolveBinding();
            if(b instanceof IFunction){
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
}
