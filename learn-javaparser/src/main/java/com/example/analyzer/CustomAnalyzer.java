package com.example.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class CustomAnalyzer {
    private String codePath;
    private List<Check> checks = new ArrayList<>();

    public CustomAnalyzer(String codePath, List<String> enabledChecks) {
        this.codePath = codePath;
        for (String checkClassName : enabledChecks) {
            try {
                Class<?> clazz = Class.forName("com.example.analyzer." + checkClassName);
                Constructor<?> constructor = clazz.getConstructor();
                Check check = (Check) constructor.newInstance();
                checks.add(check);
            } catch (Exception e) {
                AnalyzerLogger.log("Failed to load check: " + checkClassName + " - " + e.getMessage());
            }
        }
    }

    public CheckResult runChecks() {
        CheckResult result = new CheckResult();
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(codePath));
            for (Check check : checks) {
                check.performCheck(cu, result);
            }
            result.printIssues();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
}
