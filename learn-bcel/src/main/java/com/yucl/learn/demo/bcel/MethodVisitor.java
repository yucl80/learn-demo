package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Map;

public class MethodVisitor extends EmptyVisitor {

    private JavaClass javaClass;
    private MethodGen mg;
    private ConstantPoolGen cpg;

    private LineNumberTable lineNumberTable;

    private InstructionHandle ih;

    private Map<String,Object> fieldMap = new HashMap<>();

    public MethodVisitor(MethodGen mg, JavaClass javaClass) {
        this.javaClass = javaClass;
        this.mg = mg;
        cpg = mg.getConstantPool();

        lineNumberTable = mg.getLineNumberTable(cpg);
    }

    @Override
    public void visitGETFIELD(GETFIELD getfield) {
        super.visitGETFIELD(getfield);
       // System.out.println(getfield.getName());
    }

    @Override
    public void visitPUTFIELD(PUTFIELD putfield) {
        super.visitPUTFIELD(putfield);
        if(this.mg.getMethod().getName().equals("<init>")){
            System.out.println(putfield.getName(cpg));
        }
    }

    @Override
    public void visitGETSTATIC(GETSTATIC getstatic) {
        super.visitGETSTATIC(getstatic);
        //System.out.println(getstatic.getName());
        if (getstatic.getFieldName(cpg).equals("map") && this.mg.getMethod().getName().equals("<clinit>")) {
            // System.out.println(ih.getNext().getNext().getInstruction());
            ih = ih.getNext();
            Object key = ((LDC) ih.getInstruction()).getValue(cpg);
            ih = ih.getNext();
            Object value = ((LDC) ih.getInstruction()).getValue(cpg);
            System.out.println("key：" + key + " ; value:" + value);
        }
    }

    @Override
    public void visitPUTSTATIC(PUTSTATIC putstatic) {
        super.visitPUTSTATIC(putstatic);
        //  System.out.println(putstatic.getName());

    }

    public void beforeStart() {
        // 生成格式化后的完整方法名

        if (!mg.isSynthetic()) {
            /*
                对于有synthetic标记的方法，不进行以下处理
                1. 避免出现方法上的重复注解
                2. 避免出现方法对应行号信息重复记录
             */

            // 记录方法上的注解信息

            // 记录当前方法对应的起止行号
            if (lineNumberTable != null) {
                LineNumber[] lineNumbers = lineNumberTable.getLineNumberTable();
                if (lineNumbers != null && lineNumbers.length > 0) {
                    int minLineNumber = lineNumbers[0].getLineNumber();
                    int maxLineNumber;
                    if (lineNumbers.length == 1) {
                        maxLineNumber = minLineNumber;
                    } else {
                        maxLineNumber = lineNumbers[lineNumbers.length - 1].getLineNumber();
                    }

                }
            }
        }
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
