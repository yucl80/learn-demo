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
            String file = "D:\\MethodDiff.java";
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
            CompilationUnit unit = result.getResult().get();
            unit.getChildNodes().stream().forEach(node -> {

            });
            System.out.println(result.getResult());
        }

    }
}
