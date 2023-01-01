package com.yucl.learn.demo.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Parser {
    public static void main(String[] args) {
        try {
            String file = "D:\\workspaces\\IdeaProjects\\learn-demo\\learn-javaparser\\src\\main\\java\\com\\yucl\\learn\\demo\\javaparser\\MethodDiff.java";
            String contents = new String(Files.readAllBytes(Paths.get(file)));
            test(contents);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static void test(String contents) {
        StringReader reader = new StringReader(contents);
        ParseResult<CompilationUnit> result = new JavaParser().parse(reader);
        if(result.isSuccessful()){
            System.out.println(result.getResult());
        }

    }
}
