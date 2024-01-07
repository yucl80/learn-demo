package com.yucl.learn.demo.bcel;


import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.JavaClass;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.util.*;


public class App {
    private static  Set<String> refBeanSet = new HashSet<>();
    private static Set<String> dubboInterfaceSet = new HashSet<>();

    /**
     *  需要先遍历一次所有的类，以便获取所有类，类继承关系，DubboReference配置，然后再处理属性字段和方法的注解
     * @param args
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {
       // ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\demo_jdk11\\target\\classes"));
       // JavaClass clazz = repository.loadClass("com.yucl.demo.controller.TestController");
       // ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\dubbo-samples\\1-basic\\dubbo-samples-api\\target\\classes"));
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\dubbo-samples\\2-advanced\\dubbo-samples-autowire\\dubbo-samples-autowire-provider\\target\\classes;D:\\workspaces\\dubbo-samples\\2-advanced\\dubbo-samples-autowire\\dubbo-samples-autowire-interface\\target\\classes"));


        JavaClass clazz = repository.loadClass("org.apache.dubbo.samples.autowire.provider.HelloServiceImpl");
        AnnoInfo annoInfo = new AnnoInfo();
        Arrays.stream(clazz.getAnnotationEntries()).forEach(anno -> {
            String annoType = anno.getAnnotationType();
            System.out.println(annoType);
            //todo 考虑继承的子类
            if ("Lorg/springframework/web/bind/annotation/RestController;".equals(annoType)
                    || "Lorg/springframework/web/bind/annotation/Controller;".equals(annoType)) {
                annoInfo.setController(true);
            }else if("Lorg/apache/dubbo/config/annotation/DubboService;".equals(annoType)){
                dubboInterfaceSet.addAll(Arrays.asList(clazz.getInterfaceNames()));
            }else if ("Lorg/springframework/web/bind/annotation/RequestMapping;".equals(annoType)) {
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

            });

        }


        Arrays.stream(clazz.getMethods()).forEach(method -> {
            for(AnnotationEntry anno : method.getAnnotationEntries()){
                String annoType = anno.getAnnotationType();
                if("Lorg/apache/dubbo/config/annotation/DubboReference;".equals(annoType)){
                    // ()Lorg/apache/dubbo/config/spring/ReferenceBean<>
                    if("Lorg/apache/dubbo/config/spring/ReferenceBean;".equals(method.getReturnType().getSignature())) {
                        String genericSignature = method.getGenericSignature();
                        String beanType = genericSignature.substring(48, genericSignature.length() - 2);
                        refBeanSet.add(beanType);
                    }
                }
            }
        });

        Map<Type,String> refMap = new HashMap<>();
        Arrays.stream(clazz.getFields()).forEach(field -> {
            Arrays.stream(field.getAnnotationEntries()).forEach(anno -> {
                String annoType = anno.getAnnotationType();
                Type fieldType = field.getType();
                if ("Lorg.apache.dubbo.config.annotation.DubboReference;".equals(annoType)) {
                    refMap.put(fieldType, field.getName());
                }else if("Lorg/springframework/beans/factory/annotation/Autowired;".equals(annoType) && refBeanSet.contains(fieldType.getSignature())){
                    refMap.put(fieldType, field.getName());
                }
            });
        });
        System.out.println(refMap);

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
