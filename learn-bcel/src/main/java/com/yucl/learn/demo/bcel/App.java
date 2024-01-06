package com.yucl.learn.demo.bcel;


import org.apache.bcel.classfile.JavaClass;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.util.*;


public class App {
    public static void main(String[] args) throws ClassNotFoundException {
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\demo_jdk11\\target\\classes"));
        JavaClass clazz = repository.loadClass("com.yucl.demo.controller.TestController");
        AnnoInfo annoInfo = new AnnoInfo();
        Arrays.stream(clazz.getAnnotationEntries()).forEach(anno -> {
            String annoType = anno.getAnnotationType();
            //todo 考虑继承的子类
            if ("Lorg/springframework/web/bind/annotation/RestController;".equals(annoType)
                    || "Lorg/springframework/web/bind/annotation/Controller;".equals(annoType)) {
                annoInfo.setController(true);
            }
            if ("Lorg/springframework/web/bind/annotation/RequestMapping;".equals(annoType)) {
                Arrays.stream(anno.getElementValuePairs()).forEach(vp -> {
                    if ("value".equals(vp.getNameString())) {
                        String path = vp.getValue().toString();
                        path = path.substring(1, path.length() - 1);
                        annoInfo.setBasePath(path);
                    }
                });
            }
        });

        if (annoInfo.isController()) {
            Map<String, Method> apiMap = new HashMap<>();
            Arrays.stream(clazz.getMethods()).forEach(method -> {
                Arrays.stream(method.getAnnotationEntries()).forEach(anno -> {
                    String annoType = anno.getAnnotationType();
                    if ("Lorg/springframework/web/bind/annotation/GetMapping;".equals(annoType) ||
                            "Lorg/springframework/web/bind/annotation/PostMapping;".equals(annoType) ||
                            "Lorg/springframework/web/bind/annotation/PutMapping;".equals(annoType) ||
                            "Lorg/springframework/web/bind/annotation/RequestMapping;".equals(annoType) ||
                            "Lorg/springframework/web/bind/annotation/DeleteMapping;".equals(annoType)) {

                        Arrays.stream(anno.getElementValuePairs()).forEach(vp -> {
                            String path = vp.getValue().toString();
                            path = path.substring(1, path.length() - 1);
                            String url = annoInfo.getBasePath() + path;
                            apiMap.put(url, method);
                        });
                    }
                });

                ConstantPoolGen cpg = new ConstantPoolGen(clazz.getConstantPool());
                MethodGen mg = new MethodGen(method, clazz.getClassName(), cpg);
                MethodVisitor visitor = new MethodVisitor(mg, clazz);
                visitor.beforeStart();
                visitor.start();
            });
            // System.out.println(apiMap);
        }
        Map<Type,String> refMap = new HashMap<>();
        Arrays.stream(clazz.getFields()).forEach(field -> {
            Arrays.stream(field.getAnnotationEntries()).forEach(anno -> {
                String annoType = anno.getAnnotationType();
                if ("Lorg/springframework/beans/factory/annotation/Autowired;".equals(annoType) ||
                        "Lorg.apache.dubbo.config.annotation.DubboReference;".equals(annoType)) {
                    //System.out.println(field.getName() + ":" + field.getType());
                    refMap.put(field.getType(), field.getName());
                }
            });
        });

        getApiCall(clazz,refMap);

    }


    private static void getApiCall(JavaClass clazz, Map<Type, String> refMap) {
        try {
            Arrays.stream(clazz.getMethods()).forEach(method -> {
                ConstantPoolGen cpg = new ConstantPoolGen(clazz.getConstantPool());
                MethodGen mg = new MethodGen(method, clazz.getClassName(), cpg);
                MethodCallVisitor visitor = new MethodCallVisitor(mg, clazz,refMap);
                visitor.start();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
