package com.example.analyzer;

import java.util.Arrays;
import java.util.List;

public class JavaLanguageAdapter implements LanguageAdapter {

    private List<Check> checks;

    @Override
    public void parse(String codePath) {
        // 使用JavaParser解析Java代码
    }

    @Override
    public List<Check> getChecks() {
        return Arrays.asList(
                new UnusedImportsCheck(),
                new DeadCodeCheck(),
                new LongMethodCheck(),
                new UnclosedResourceCheck(),
                new UnhandledExceptionCheck(),
                new UnusedVariableCheck()
        );
    }
}
