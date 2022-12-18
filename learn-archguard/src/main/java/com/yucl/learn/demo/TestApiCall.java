package com.yucl.learn.demo;

import chapi.domain.core.CodeDataStruct;
import org.archguard.scanner.analyser.ApiCallAnalyser;
import org.archguard.scanner.analyser.JavaAnalyser;
import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.context.AnalyserType;
import org.archguard.scanner.core.sourcecode.ContainerService;
import org.archguard.scanner.core.sourcecode.SourceCodeContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestApiCall {
    public static void main(String[] args) {
        JavaAnalyser javaAnalyser = new JavaAnalyser(new TestSourceCodeContext());

        ApiCallAnalyser analyser =new ApiCallAnalyser(new TestSourceCodeContext() );

        List<ContainerService> result = analyser.analyse(javaAnalyser.analyse());

        System.out.println(result);
    }
}
