package com.yucl.learn.demo.bcel;



import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Visitor;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPathRepository;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ClassNotFoundException {
        ClassPathRepository repository = new ClassPathRepository(new ClassPath("D:\\workspaces\\IdeaProjects\\learn-demo\\demoproject\\target\\classes"));
        JavaClass clazz = repository.loadClass("com.example.demo.ServiceBImpl");
        Arrays.stream(clazz.getFields()).forEach(field -> {
            System.out.println(field);
        });



        Arrays.stream(clazz.getMethods()).forEach(method -> {
                    System.out.println(method.getName());
            ConstantPoolGen cpg = new ConstantPoolGen(clazz.getConstantPool());;
            MethodGen mg = new MethodGen(method, clazz.getClassName(), cpg);
            MethodVisitor visitor = new MethodVisitor(mg, clazz);
            visitor.beforeStart();
            visitor.start();



        });

    }
}
