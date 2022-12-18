package com.yucl.learn.demo;

import chapi.domain.core.CodeDataStruct;
import org.archguard.scanner.analyser.DataMapAnalyser;
import org.archguard.scanner.analyser.JavaAnalyser;
import org.archguard.scanner.core.sourcecode.CodeDatabaseRelation;

import java.util.List;

public class TestDataMap {
    public static void main(String[] args) {
        TestSourceCodeContext sourceCodeContext = new TestSourceCodeContext();

        DataMapAnalyser dataMapAnalyser = new DataMapAnalyser(sourceCodeContext);

        JavaAnalyser javaAnalyser = new JavaAnalyser(sourceCodeContext);
        List<CodeDataStruct> codeDataStructList = javaAnalyser.analyse();
        List<CodeDatabaseRelation> result = dataMapAnalyser.analyse(codeDataStructList);

        result.forEach(codeDatabaseRelation->{
            System.out.println(codeDatabaseRelation.getFunctionName() + "  " + codeDatabaseRelation.getTables());
        });
    }
}
