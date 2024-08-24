package com.yucl.learn.demo.bcel;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.util.ArrayList;
import java.util.List;

public class BCELMethodParameterExample2 {
    public static void main(String[] args) throws Exception {
        // 加载目标类
        String className = "com.example.demo.BizServiceImpl";
//        Repository repository = new ClassLoaderRepository(ClassLoader.getSystemClassLoader()));
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\learn-demo\\demoproject\\target\\classes"));
        JavaClass javaClass = repository.loadClass(className);
        ClassGen classGen = new ClassGen(javaClass);
        for (Method method : classGen.getMethods()) {
            System.out.println("method :" + method.getName() + "   " +method.getSignature() + "    " + method.getGenericSignature() );
            List<String>  paramNameList = new ArrayList<>();
            Code code = method.getCode();
            if(code != null) {
                LocalVariableTable localVariableTable = code.getLocalVariableTable();
                if (localVariableTable != null) {
                    for (int i = 0; i < method.getArgumentTypes().length; i++) {
                        LocalVariable localVariable = localVariableTable.getLocalVariable(i + 1,0);
                        if (localVariable != null) {
                            paramNameList.add(localVariable.getName());
                        }
                    }
                }
            }else  {
                boolean MethodParametersFlag = false;
                for (Constant constant : method.getConstantPool()) {
                    if (constant != null) {
                        if (constant instanceof ConstantUtf8) {
                            String str = ((ConstantUtf8) constant).getBytes();
                            if ("Signature".equals(str)) {
                                break;
                            }
                            if (MethodParametersFlag) {
                                paramNameList.add(str);

                            }
                            if ("MethodParameters".equals(str)) {
                                MethodParametersFlag = true;
                            }

                        }
                    }

                }
            }
            System.out.println(paramNameList);

        }
    }


}
