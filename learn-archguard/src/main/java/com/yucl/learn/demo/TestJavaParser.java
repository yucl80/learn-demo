package com.yucl.learn.demo;

import chapi.domain.core.CodeDataStruct;
import org.archguard.scanner.analyser.JavaAnalyser;
import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.context.AnalyserType;
import org.archguard.scanner.core.sourcecode.SourceCodeContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class TestJavaParser {
    public static void main(String[] args) {
        JavaAnalyser javaAnalyser = new JavaAnalyser(new TestSourceCodeContext());

        List<CodeDataStruct> result = javaAnalyser.analyse();

        result.forEach(codeDataStruct -> {
            System.out.println(codeDataStruct.getClassFullName());
            if(codeDataStruct.getExtend() != null){
                System.out.println(codeDataStruct.getClassFullName() + " : " + codeDataStruct.getExtend());
            }

            Arrays.stream(codeDataStruct.getImplements()).forEach(impl -> {
                System.out.println(codeDataStruct.getClassFullName() + " : " + impl);
            });
        });


       result.forEach(codeDataStruct -> {
            Arrays.stream(codeDataStruct.getFunctions()).forEach(codeFunction -> {
                Arrays.stream(codeFunction.getFunctionCalls()).forEach(codeCall -> {
                    System.out.println(codeDataStruct.getClassFullName() + "  :  " + codeFunction.getName() + " -> " + codeCall.buildFullMethodName());
                });
            });
        });

        result.forEach(codeDataStruct -> {
            Arrays.stream(codeDataStruct.getAnnotations()).forEach(codeAnnotation -> {
                System.out.println(codeDataStruct.getClassFullName() + "  :  " + codeAnnotation.getName() + "    " + Arrays.asList(codeAnnotation.getKeyValues()));
            });
        });

    }
}
