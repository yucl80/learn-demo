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

public class BCELMethodParameterExample3 {
    public static void main(String[] args) throws Exception {
        // 加载目标类
        String className = "com.example.demo.BizService";
//        Repository repository = new ClassLoaderRepository(ClassLoader.getSystemClassLoader()));
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\learn-demo\\demoproject\\target\\classes"));
        JavaClass javaClass = repository.loadClass(className);
        ClassGen classGen = new ClassGen(javaClass);
        for (Field field : classGen.getFields()) {
            System.out.println(field.getName() + "   " + field.getSignature() + "   " + field.getGenericSignature());
        }

        for (Method method : classGen.getMethods()) {
//            System.out.println("method :" + method.getName() + "   " + method.getSignature() + "    " + method.getGenericSignature());
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
        if (method.getGenericSignature() != null) {
            argumentTypes = parseGenericSignature(method.getGenericSignature());
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
        return String.format("%s %s(%s);", getShortName(returnType), method.getName(), parameters);
    }

    private static List<String> parseGenericSignature(String text) {
        text = text.substring(0, text.lastIndexOf(')') + 1);
        List<String> matches = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        for (int i = 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == 'L' && stack.isEmpty()) {
                stack.push(i);
            } else if (c == '<' && text.charAt(i + 1) == 'L') {
                stack.push(i);
            } else if (c == ';') {
                int begin = stack.pop();
                if (stack.isEmpty()) {
                    String exp = text.substring(begin + 1, i);
                    exp = exp.replaceAll("<L", "<");
                    exp = exp.replaceAll(";>", ">");
                    exp = exp.replaceAll(";L", ",");
                    exp = exp.replaceAll("/", ".");
                    matches.add(exp);
                }
            } else {
                if (stack.isEmpty()) {
                    Type[] t = Type.getArgumentTypes("(" + c + ")");
                    Arrays.asList(t).forEach(s -> matches.add(s.toString()));
                } else {
                    if (c == 'L' && text.charAt(i - 1) == ';') {
                        stack.push(i);
                    }
                }
            }
        }
        return matches;
    }


    public static String getShortName(String className) {
        String regex = "\\b(?:[a-z]+\\.)+([A-Z][a-zA-Z0-9]*)\\b";
        return className.replaceAll(regex, "$1");
    }

}
