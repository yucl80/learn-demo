package com.yucl.learn.demo.jdt.sql;

import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        //从文件读取源码
        String source = new String(Files.readAllBytes(Paths.get("D:\\workspaces\\python_projects\\sample\\sql_13.txt")));
         source = "public class Example { "+ source +" }";
        CompilationUnit cu = JDTParser.parse(source);
        SQLInjectionVisitor visitor = new SQLInjectionVisitor();
        cu.accept(visitor);
    }
}

