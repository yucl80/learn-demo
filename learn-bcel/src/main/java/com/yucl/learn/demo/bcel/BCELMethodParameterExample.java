package com.yucl.learn.demo.bcel;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class BCELMethodParameterExample {
    public static void main(String[] args) throws Exception {
        // 加载目标类
        String className = "com.example.demo.BizServiceImpl";
//        Repository repository = new ClassLoaderRepository(ClassLoader.getSystemClassLoader()));
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\learn-demo\\demoproject\\target\\classes"));
        JavaClass javaClass = repository.loadClass(className);
        ClassGen classGen = new ClassGen(javaClass);
        for (Field field : classGen.getFields()) {
            System.out.println(field.getName() + "   " + field.getSignature() + "   " +  field.getGenericSignature());
            if(field.getGenericSignature() != null){
                System.out.println(field.getName() + "   "  + Arrays.asList(Utility.methodSignatureArgumentTypes('(' +field.getGenericSignature()+")V")));
            }else {
                System.out.println(field.getName() + "   " + getShortName(field.getSignature()));
            }
        }

        for (Method method : classGen.getMethods()) {
           System.out.println("method :" + method.getName() + "   " + method.getSignature() + "    " + method.getGenericSignature());
           if(method.getGenericSignature() != null) {
              String[] argumentTypes =  Utility.methodSignatureArgumentTypes(method.getGenericSignature());
              System.out.println(new ArrayList<>(Arrays.asList(argumentTypes)));
              String  returnType = Utility.methodSignatureReturnType(method.getGenericSignature());
              System.out.println(returnType);
           }
            List<String> paramNameList = new ArrayList<>();
            Code code = method.getCode();
            if (code != null) {
                LocalVariableTable localVariableTable = code.getLocalVariableTable();
                if (localVariableTable != null) {
                    for (int i = 0; i < method.getArgumentTypes().length; i++) {
                        if (method.isStatic()) {
                            LocalVariable localVariable = localVariableTable.getLocalVariable(i, 0);
                            if (localVariable != null) {
                                paramNameList.add(localVariable.getName());
                            }
                        } else {
                            LocalVariable localVariable = localVariableTable.getLocalVariable(i + 1, 0);
                            if (localVariable != null) {
                                paramNameList.add(localVariable.getName());
                            }
                        }

                    }
                }
            } else {
                for (Attribute attribute : method.getAttributes()) {
                    if (attribute.getName().equals("MethodParameters")) {
                        MethodParameters methodParameters = method.getAttribute(Const.ATTR_METHOD_PARAMETERS);
                        for (MethodParameter param : methodParameters.getParameters()) {
                            String paramName = param.getParameterName(method.getConstantPool());
                            paramNameList.add(paramName);
                        }
                        break;
                    }
                }

            }
            if (paramNameList.isEmpty() && method.getArgumentTypes().length != 0) {
                MethodGen methodGen = new MethodGen(method, classGen.getClassName(), classGen.getConstantPool());
                if (methodGen.getArgumentNames() != null) {
                    paramNameList.addAll(Arrays.asList(methodGen.getArgumentNames()));
                }
            }

            String fullName = buildMethodSignature(method, paramNameList);
            System.out.println(fullName);

        }
    }

    private static String buildMethodSignature(Method method, List<String> paramNameList) {
        String methodSignature = method.getSignature();
        List<String> argumentTypes = null;
        String genericSignature = method.getGenericSignature();
        if (genericSignature != null) {
            argumentTypes = Arrays.asList(Utility.methodSignatureArgumentTypes(genericSignature));
        } else {
            Type[] types = Type.getArgumentTypes(methodSignature);
            argumentTypes = Arrays.stream(types).map(Type::toString).collect(Collectors.toList());
        }
        StringBuilder parameters = new StringBuilder();
        for (int i = 0; i < argumentTypes.size(); i++) {
            String typeShortName = getShortName(argumentTypes.get(i));
            parameters.append(typeShortName).append(" ").append(paramNameList.get(i));
            if (i < argumentTypes.size() - 1) {
                parameters.append(", ");
            }
        }
        String returnType = Type.getReturnType(method.getSignature()).toString();
        if(genericSignature!=null){
            returnType = Utility.methodSignatureReturnType(genericSignature);
        }
        if(genericSignature != null && genericSignature.startsWith("<")){
            String tt = genericSignature.substring(1,genericSignature.indexOf(">"));
            System.out.println("tt:" + tt);
            String result = tt.replaceAll(":[^;]+", "").replaceAll(";$", "").replaceAll(";",",");
            System.out.println("result:" + "<"+result+">");
            return String.format(" <%s> %s %s(%s);", result, getShortName(returnType), method.getName(), parameters);
        }else {
            return String.format("%s %s(%s);", getShortName(returnType), method.getName(), parameters);
        }

    }


    public static String getShortName(String className) {
        String regex = "\\b(?:[a-z]+\\.)+([A-Z][a-zA-Z0-9]*)\\b";
        return className.replaceAll(regex, "$1");
    }

}
