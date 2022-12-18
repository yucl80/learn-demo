package com.yucl.learn.demo;

import org.archguard.scanner.core.client.ArchGuardClient;
import org.archguard.scanner.core.context.AnalyserType;
import org.archguard.scanner.core.sourcecode.SourceCodeContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestSourceCodeContext implements SourceCodeContext {
    @NotNull
    @Override
    public AnalyserType getType() {
        return AnalyserType.SOURCE_CODE;
    }

    @NotNull
    @Override
    public String getLanguage() {
        return "java";
    }

    @NotNull
    @Override
    public List<String> getFeatures() {
        return null;
    }

    @NotNull
    @Override
    public String getPath() {
        return "D:\\Downloads\\demo\\demo\\src\\main\\java";
    }

    @NotNull
    @Override
    public ArchGuardClient getClient() {
        return new TestArchGuardClient();
    }
}
