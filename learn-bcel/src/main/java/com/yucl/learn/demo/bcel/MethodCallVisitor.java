package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Map;

public class MethodCallVisitor extends EmptyVisitor {

    private JavaClass javaClass;
    private MethodGen mg;
    private ConstantPoolGen cpg;

    private LineNumberTable lineNumberTable;

    private InstructionHandle ih;

    private  Map<Type, String> refMap;

    private Map<String,Object> fieldMap = new HashMap<>();

    public MethodCallVisitor(MethodGen mg, JavaClass javaClass, Map<Type, String> refMap) {
        this.javaClass = javaClass;
        this.mg = mg;
        this.refMap = refMap;
        cpg = mg.getConstantPool();
        lineNumberTable = mg.getLineNumberTable(cpg);
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL invokevirtual) {
        super.visitINVOKEVIRTUAL(invokevirtual);
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE invokeinterface) {
        ReferenceType refType = invokeinterface.getReferenceType(cpg);
        if(refMap.containsKey(refType)){
            System.out.println("call rpc :" + invokeinterface.getMethodName(cpg) +":" + invokeinterface.getSignature(cpg));
        }
        for(InstructionHandle inst = ih.getPrev(); inst != null ;inst= inst.getPrev()){
            if("getfield".equals(inst.getInstruction().getName())){
                GETFIELD getfield = (GETFIELD) inst.getInstruction();
                if(getfield.getName(cpg).equals(refMap.get(refType))){
                    System.out.println("found ref filed ");
                }
            }

        }
        System.out.println(invokeinterface.getName(cpg));
        super.visitINVOKEINTERFACE(invokeinterface);
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC obj) {
        super.visitINVOKEDYNAMIC(obj);
    }



    public void start() {
        if (mg.isAbstract() || mg.isNative()) {
            return;
        }

        for (ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();


            // if (i instanceof InvokeInstruction) {
            i.accept(this);
            //  }
        }
    }

}
