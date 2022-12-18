package com.yucl.learn.demo;

import org.archguard.scanner.analyser.DataMapAnalyser;
import org.archguard.scanner.analyser.JavaAnalyser;
import org.archguard.scanner.core.sourcecode.CodeDatabaseRelation;

import java.util.List;

public class TestDataMap {
    public static void main(String[] args) {
        TestSourceCodeContext sourceCodeContext = new TestSourceCodeContext();

        DataMapAnalyser dataMapAnalyser = new DataMapAnalyser(sourceCodeContext);

        JavaAnalyser javaAnalyser = new JavaAnalyser(sourceCodeContext);

        List<CodeDatabaseRelation> result = dataMapAnalyser.analyse(javaAnalyser.analyse());

        result.forEach(codeDatabaseRelation->{
            System.out.println(codeDatabaseRelation.getFunctionName() + "  " + codeDatabaseRelation.getTables());
        });
    }
}
